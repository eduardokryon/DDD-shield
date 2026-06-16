package com.dddlock.domain.usecase

import com.dddlock.model.DDD
import com.dddlock.domain.repository.DDDRepository

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
class GetAllDDDsUseCase(
    private val repository: DDDRepository
) {
    operator fun invoke(): List<DDD> {
        return repository.getAllDDDs()
    }
}
