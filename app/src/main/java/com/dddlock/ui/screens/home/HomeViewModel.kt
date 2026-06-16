package com.dddlock.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dddlock.domain.model.BlockerStatus
import com.dddlock.domain.usecase.GetBlockerStatusUseCase
import com.dddlock.domain.usecase.GetAllDDDsUseCase
import com.dddlock.domain.usecase.GetBlockedDDDsUseCase
import com.dddlock.domain.usecase.SearchDDDsUseCase
import com.dddlock.domain.usecase.SetBlockerEnabledUseCase
import com.dddlock.domain.usecase.ToggleDDDBlockUseCase
import com.dddlock.model.DDD
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
data class HomeUiState(
    val blockerStatus: BlockerStatus = BlockerStatus(isEnabled = false, blockedCount = 0),
    val allDDDs: List<DDD> = emptyList(),
    val filteredDDDs: List<DDD> = emptyList(),
    val blockedCodes: Set<String> = emptySet(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val getAllDDDs: GetAllDDDsUseCase,
    private val getBlockedDDDs: GetBlockedDDDsUseCase,
    private val getBlockerStatus: GetBlockerStatusUseCase,
    private val toggleDDDBlock: ToggleDDDBlockUseCase,
    private val searchDDDs: SearchDDDsUseCase,
    private val setBlockerEnabled: SetBlockerEnabledUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val allDDDs = getAllDDDs()
        _uiState.value = _uiState.value.copy(
            allDDDs = allDDDs,
            filteredDDDs = allDDDs
        )

        viewModelScope.launch {
            combine(
                getBlockedDDDs(),
                getBlockerStatus()
            ) { blockedCodes, status ->
                Pair(blockedCodes, status)
            }.collect { (blockedCodes, status) ->
                _uiState.value = _uiState.value.copy(
                    blockedCodes = blockedCodes,
                    blockerStatus = status
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        val results = searchDDDs(query)
        _uiState.value = _uiState.value.copy(filteredDDDs = results)
    }

    fun onToggleDDD(code: String) {
        val isCurrentlyBlocked = code in _uiState.value.blockedCodes
        viewModelScope.launch {
            toggleDDDBlock(code, !isCurrentlyBlocked)
        }
    }

    fun onToggleBlocker() {
        viewModelScope.launch {
            val newState = !_uiState.value.blockerStatus.isEnabled
            setBlockerEnabled(newState)
        }
    }
}
