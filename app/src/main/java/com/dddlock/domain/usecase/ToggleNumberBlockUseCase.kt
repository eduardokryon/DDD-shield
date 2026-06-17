package com.dddlock.domain.usecase

import com.dddlock.domain.repository.DDDRepository

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
class ToggleNumberBlockUseCase(
    private val repository: DDDRepository
) {
    suspend operator fun invoke(number: String, block: Boolean) {
        repository.toggleNumber(number, block)
    }
}
