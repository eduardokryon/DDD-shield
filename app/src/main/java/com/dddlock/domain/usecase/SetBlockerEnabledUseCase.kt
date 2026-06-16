package com.dddlock.domain.usecase

import com.dddlock.domain.repository.DDDRepository

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
class SetBlockerEnabledUseCase(
    private val repository: DDDRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        repository.setBlockerEnabled(enabled)
    }
}
