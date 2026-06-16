package com.dddlock.domain.usecase

import com.dddlock.domain.repository.DDDRepository

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
class ToggleDDDBlockUseCase(
    private val repository: DDDRepository
) {
    suspend operator fun invoke(code: String, block: Boolean) {
        repository.toggleDDD(code, block)
    }
}
