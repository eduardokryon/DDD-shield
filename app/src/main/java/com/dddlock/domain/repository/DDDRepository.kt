package com.dddlock.domain.repository

import com.dddlock.domain.model.BlockerStatus
import com.dddlock.model.DDD
import kotlinx.coroutines.flow.Flow

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
interface DDDRepository {
    fun getAllDDDs(): List<DDD>
    fun getBlockedDDDs(): Flow<Set<String>>
    fun getBlockedNumbers(): Flow<Set<String>>
    fun getBlockerStatus(): Flow<BlockerStatus>
    fun getBlockerEnabled(): Flow<Boolean>
    fun searchDDDs(query: String): List<DDD>
    suspend fun toggleDDD(code: String, block: Boolean)
    suspend fun toggleNumber(number: String, block: Boolean)
    suspend fun setBlockerEnabled(enabled: Boolean)
    suspend fun clearAllBlocked()
    suspend fun getBlockedDDDsOnce(): Set<String>
    suspend fun getBlockedNumbersOnce(): Set<String>
}
