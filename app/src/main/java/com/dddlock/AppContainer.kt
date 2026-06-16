package com.dddlock

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dddlock.data.local.DDDSettingsDataStore
import com.dddlock.data.repository.DDDRepositoryImpl
import com.dddlock.domain.repository.DDDRepository
import com.dddlock.domain.usecase.GetAllDDDsUseCase
import com.dddlock.domain.usecase.GetBlockedDDDsUseCase
import com.dddlock.domain.usecase.GetBlockerStatusUseCase
import com.dddlock.domain.usecase.SearchDDDsUseCase
import com.dddlock.domain.usecase.SetBlockerEnabledUseCase
import com.dddlock.domain.usecase.ToggleDDDBlockUseCase
import com.dddlock.ui.screens.diagnosis.DiagnosisViewModel
import com.dddlock.ui.screens.home.HomeViewModel

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
class AppContainer(val context: Context) {

    val dataStore: DDDSettingsDataStore by lazy {
        DDDSettingsDataStore(context)
    }

    val repository: DDDRepository by lazy {
        DDDRepositoryImpl(dataStore)
    }

    val getAllDDDsUseCase by lazy { GetAllDDDsUseCase(repository) }
    val getBlockedDDDsUseCase by lazy { GetBlockedDDDsUseCase(repository) }
    val getBlockerStatusUseCase by lazy { GetBlockerStatusUseCase(repository) }
    val toggleDDDBlockUseCase by lazy { ToggleDDDBlockUseCase(repository) }
    val searchDDDsUseCase by lazy { SearchDDDsUseCase(repository) }
    val setBlockerEnabledUseCase by lazy { SetBlockerEnabledUseCase(repository) }
}

class DDDViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(
                    getAllDDDs = container.getAllDDDsUseCase,
                    getBlockedDDDs = container.getBlockedDDDsUseCase,
                    getBlockerStatus = container.getBlockerStatusUseCase,
                    toggleDDDBlock = container.toggleDDDBlockUseCase,
                    searchDDDs = container.searchDDDsUseCase,
                    setBlockerEnabled = container.setBlockerEnabledUseCase
                ) as T
            }
            modelClass.isAssignableFrom(DiagnosisViewModel::class.java) -> {
                DiagnosisViewModel(
                    application = container.context as Application,
                    getBlockedDDDs = container.getBlockedDDDsUseCase
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
