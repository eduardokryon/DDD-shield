package com.dddlock.data.repository

import com.dddlock.data.local.DDDSettingsDataStore
import com.dddlock.domain.model.BlockerStatus
import com.dddlock.domain.repository.DDDRepository
import com.dddlock.model.DDD
import com.dddlock.model.allDDDs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DDDLock
 * Criado por Eduardo Brito
 * GitHub: @eduardokryon
 */
class DDDRepositoryImpl(
    private val dataStore: DDDSettingsDataStore
) : DDDRepository {

    override fun getAllDDDs(): List<DDD> = allDDDs

    override fun getBlockedDDDs(): Flow<Set<String>> = dataStore.blockedDDDs

    override fun getBlockerStatus(): Flow<BlockerStatus> {
        return combine(
            dataStore.isBlockerEnabled,
            dataStore.blockedDDDs
        ) { enabled, blocked ->
            BlockerStatus(
                isEnabled = enabled,
                blockedCount = blocked.size
            )
        }
    }

    override suspend fun toggleDDD(code: String, block: Boolean) {
        val current = dataStore.blockedDDDs.first()
        val updated = if (block) current + code else current - code
        dataStore.setBlockedDDDs(updated)
    }

    override suspend fun getBlockedDDDsOnce(): Set<String> {
        return dataStore.getBlockedDDDsOnce()
    }

    override suspend fun setBlockerEnabled(enabled: Boolean) {
        dataStore.setBlockerEnabled(enabled)
    }

    override suspend fun clearAllBlocked() {
        dataStore.setBlockedDDDs(emptySet())
    }

    override fun searchDDDs(query: String): List<DDD> {
        if (query.isBlank()) return allDDDs
        val normalized = query.lowercase().trim()
        return allDDDs.filter { ddd ->
            ddd.code.contains(normalized) ||
            ddd.city.lowercase().contains(normalized) ||
            ddd.state.lowercase().contains(normalized)
        }
    }

    override fun getBlockerEnabled(): Flow<Boolean> = dataStore.isBlockerEnabled
}
