package com.dddlock.ui.screens.blocked

import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dddlock.domain.usecase.GetBlockedNumbersUseCase
import com.dddlock.domain.usecase.ToggleNumberBlockUseCase
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
    val contacts: List<ContactInfo> = emptyList(),
    val filteredContacts: List<ContactInfo> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class BlockedNumbersViewModel(
    application: Application,
    private val getBlockedNumbers: GetBlockedNumbersUseCase,
    private val toggleNumberBlock: ToggleNumberBlockUseCase
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
                    val number = it.getString(0) ?: continue
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
        val isCurrentlyBlocked = _uiState.value.blockedNumbers.any { blocked ->
            blocked == number ||
            (blocked.length >= 8 && number.length >= 8 &&
             blocked.takeLast(8) == number.takeLast(8))
        }

        viewModelScope.launch {
            toggleNumberBlock(number, !isCurrentlyBlocked)
        }
    }

    fun onAddNumberManually(number: String) {
        if (number.isBlank()) return
        viewModelScope.launch {
            toggleNumberBlock(number, true)
        }
    }
}
