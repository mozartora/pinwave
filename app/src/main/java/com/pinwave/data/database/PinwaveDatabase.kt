package com.pinwave.data.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_trends")
data class SavedTrendEntity(
    @PrimaryKey val trendId: String,
    val savedAt: Long,
    val collectionId: String? = null,
)

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val emoji: String,
    val createdAt: Long,
)

@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long,
)

@Dao
interface SavedTrendDao {
    @Query("SELECT * FROM saved_trends ORDER BY savedAt DESC")
    fun observeSaved(): Flow<List<SavedTrendEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_trends WHERE trendId = :trendId)")
    fun observeIsSaved(trendId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: SavedTrendEntity)

    @Delete
    suspend fun remove(entity: SavedTrendEntity)

    @Query("DELETE FROM saved_trends WHERE trendId = :trendId")
    suspend fun removeById(trendId: String)

    @Query("UPDATE saved_trends SET collectionId = :collectionId WHERE trendId = :trendId")
    suspend fun assignToCollection(trendId: String, collectionId: String?)
}

@Dao
interface CollectionDao {
    @Query("SELECT * FROM collections ORDER BY createdAt ASC")
    fun observeAll(): Flow<List<CollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CollectionEntity)

    @Query("DELETE FROM collections WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface RecentSearchDao {
    @Query("SELECT * FROM recent_searches ORDER BY searchedAt DESC LIMIT 10")
    fun observeRecent(): Flow<List<RecentSearchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecentSearchEntity)

    @Query("DELETE FROM recent_searches")
    suspend fun clear()
}

@Database(
    entities = [SavedTrendEntity::class, CollectionEntity::class, RecentSearchEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class PinwaveDatabase : RoomDatabase() {
    abstract fun savedTrendDao(): SavedTrendDao
    abstract fun collectionDao(): CollectionDao
    abstract fun recentSearchDao(): RecentSearchDao
}
