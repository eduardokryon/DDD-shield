package com.dddlock.domain.usecase

import com.dddlock.domain.repository.DDDRepository
import kotlinx.coroutines.flow.Flow

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
class GetBlockedNumbersUseCase(
    private val repository: DDDRepository
) {
    operator fun invoke(): Flow<Set<String>> {
        return repository.getBlockedNumbers()
    }
}
