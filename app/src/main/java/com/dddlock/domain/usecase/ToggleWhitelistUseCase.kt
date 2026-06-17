package com.dddlock.domain.usecase

import com.dddlock.domain.repository.DDDRepository

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
class ToggleWhitelistUseCase(
    private val repository: DDDRepository
) {
    suspend operator fun invoke(number: String, whitelist: Boolean) {
        repository.toggleWhitelist(number, whitelist)
    }
}
