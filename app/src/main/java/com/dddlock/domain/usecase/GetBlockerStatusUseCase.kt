package com.dddlock.domain.usecase

import com.dddlock.domain.model.BlockerStatus
import com.dddlock.domain.repository.DDDRepository
import kotlinx.coroutines.flow.Flow

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
class GetBlockerStatusUseCase(
    private val repository: DDDRepository
) {
    operator fun invoke(): Flow<BlockerStatus> {
        return repository.getBlockerStatus()
    }
}
