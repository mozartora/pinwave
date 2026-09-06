package com.pinwave.data.repository

import com.pinwave.data.database.CollectionDao
import com.pinwave.data.database.CollectionEntity
import com.pinwave.data.database.RecentSearchDao
import com.pinwave.data.database.RecentSearchEntity
import com.pinwave.data.database.SavedTrendDao
import com.pinwave.data.database.SavedTrendEntity
import com.pinwave.data.mock.MockData
import com.pinwave.domain.model.Trend
import com.pinwave.domain.model.TrendCollection
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

/**
 * Local-only repository: saved trends, collections and recent searches.
 * These are user-generated app data, not Pinterest content, so Room
 * persistence is appropriate here (spec §28).
 */
@Singleton
class SavedRepository @Inject constructor(
    private val savedDao: SavedTrendDao,
    private val collectionDao: CollectionDao,
    private val searchDao: RecentSearchDao,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val savedTrends: Flow<List<Trend>> = savedDao.observeSaved().mapLatest { entities ->
        entities.mapNotNull { MockData.trend(it.trendId) }
    }

    val savedIds: Flow<Set<String>> = savedDao.observeSaved().mapLatest { list ->
        list.map { it.trendId }.toSet()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val collections: Flow<List<Pair<TrendCollection, Int>>> =
        combine(collectionDao.observeAll(), savedDao.observeSaved()) { collections, saved ->
            collections.map { entity ->
                TrendCollection(entity.id, entity.name, entity.emoji) to
                    saved.count { it.collectionId == entity.id }
            }
        }

    fun isSaved(trendId: String): Flow<Boolean> = savedDao.observeIsSaved(trendId)

    suspend fun toggleSaved(trendId: String, currentlySaved: Boolean) {
        if (currentlySaved) {
            savedDao.removeById(trendId)
        } else {
            savedDao.save(SavedTrendEntity(trendId, System.currentTimeMillis()))
        }
    }

    suspend fun createCollection(name: String, emoji: String) {
        collectionDao.upsert(
            CollectionEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                emoji = emoji,
                createdAt = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun deleteCollection(id: String) = collectionDao.delete(id)

    val recentSearches: Flow<List<String>> =
        searchDao.observeRecent().map { list -> list.map { it.query } }

    suspend fun recordSearch(query: String) {
        val q = query.trim()
        if (q.isNotEmpty()) {
            searchDao.insert(RecentSearchEntity(q, System.currentTimeMillis()))
        }
    }

    suspend fun clearRecentSearches() = searchDao.clear()
}
