package com.dddlock.ui.screens.blocked

import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dddlock.domain.usecase.GetBlockedNumbersUseCase
import com.dddlock.domain.usecase.GetWhitelistedNumbersUseCase
import com.dddlock.domain.usecase.ToggleNumberBlockUseCase
import com.dddlock.domain.usecase.ToggleWhitelistUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
data class ContactInfo(
    val name: String,
    val number: String,
    val normalizedNumber: String
)

data class BlockedNumbersUiState(
    val blockedNumbers: Set<String> = emptySet(),
    val whitelistedNumbers: Set<String> = emptySet(),
    val contacts: List<ContactInfo> = emptyList(),
    val filteredContacts: List<ContactInfo> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val selectedTab: Int = 0 // 0 = bloqueados, 1 = exceções
)

class BlockedNumbersViewModel(
    application: Application,
    private val getBlockedNumbers: GetBlockedNumbersUseCase,
    private val toggleNumberBlock: ToggleNumberBlockUseCase,
    private val getWhitelistedNumbers: GetWhitelistedNumbersUseCase,
    private val toggleWhitelist: ToggleWhitelistUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(BlockedNumbersUiState())
    val uiState: StateFlow<BlockedNumbersUiState> = _uiState.asStateFlow()

    init {
        loadContacts()

        viewModelScope.launch {
            getBlockedNumbers().collect { blocked ->
                _uiState.value = _uiState.value.copy(blockedNumbers = blocked)
            }
        }

        viewModelScope.launch {
            getWhitelistedNumbers().collect { whitelisted ->
                _uiState.value = _uiState.value.copy(whitelistedNumbers = whitelisted)
            }
        }
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val contacts = withContext(Dispatchers.IO) {
                loadContactsFromDevice()
            }

            _uiState.value = _uiState.value.copy(
                contacts = contacts,
                filteredContacts = contacts,
                isLoading = false
            )
        }
    }

    private fun loadContactsFromDevice(): List<ContactInfo> {
        val contacts = mutableListOf<ContactInfo>()
        var cursor: Cursor? = null

        try {
            cursor = getApplication<Application>().contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                arrayOf(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER
                ),
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            cursor?.let {
                while (it.moveToNext()) {
                    val name = it.getString(0) ?: continue
                    val number = it.getString(1) ?: continue
                    val digits = number.filter { c -> c.isDigit() }
                    val normalized = normalizeNumber(digits)

                    if (normalized.length >= 8) {
                        contacts.add(ContactInfo(name, number, normalized))
                    }
                }
            }
        } catch (e: Exception) {
            // Em caso de erro, retorna lista vazia
        } finally {
            cursor?.close()
        }

        return contacts.distinctBy { it.normalizedNumber.takeLast(8) }
    }

    private fun normalizeNumber(digits: String): String {
        return when {
            digits.startsWith("55") && digits.length >= 12 -> digits.substring(2)
            digits.startsWith("0") && digits.length > 2 -> digits.substring(1)
            else -> digits
        }
    }

    fun onTabSelected(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        val filtered = if (query.isBlank()) {
            _uiState.value.contacts
        } else {
            val normalized = query.lowercase().trim()
            _uiState.value.contacts.filter { contact ->
                contact.name.lowercase().contains(normalized) ||
                contact.number.contains(normalized) ||
                contact.normalizedNumber.contains(normalized)
            }
        }
        _uiState.value = _uiState.value.copy(filteredContacts = filtered)
    }

    fun onToggleNumber(number: String) {
        val isCurrentlyBlocked = isNumberInList(number, _uiState.value.blockedNumbers)
        viewModelScope.launch {
            toggleNumberBlock(number, !isCurrentlyBlocked)
        }
    }

    fun onToggleWhitelist(number: String) {
        val isCurrentlyWhitelisted = isNumberInList(number, _uiState.value.whitelistedNumbers)
        viewModelScope.launch {
            toggleWhitelist(number, !isCurrentlyWhitelisted)
        }
    }

    fun onAddNumberManually(number: String) {
        if (number.isBlank()) return
        viewModelScope.launch {
            if (_uiState.value.selectedTab == 0) {
                toggleNumberBlock(number, true)
            } else {
                toggleWhitelist(number, true)
            }
        }
    }

    private fun isNumberInList(number: String, list: Set<String>): Boolean {
        return list.any { item ->
            item == number ||
            (item.length >= 8 && number.length >= 8 &&
             item.takeLast(8) == number.takeLast(8))
        }
    }
}
