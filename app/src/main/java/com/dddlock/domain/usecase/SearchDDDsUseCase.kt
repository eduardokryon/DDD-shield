package com.dddlock.domain.usecase

import com.dddlock.model.DDD
import com.dddlock.domain.repository.DDDRepository

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
class SearchDDDsUseCase(
    private val repository: DDDRepository
) {
    operator fun invoke(query: String): List<DDD> {
        return repository.searchDDDs(query)
    }
}
