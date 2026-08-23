package com.aetheris.android.data.local.dao

import androidx.room.*

@Entity(tableName = "cached_servers")
data class CachedServer(
    @PrimaryKey val id: String,
    val name: String,
    val node: String,
    val status: String,
    val ip: String,
    val port: Int = 25565,
    val game: String = "",
    val cpuPercentage: Float = 0f,
    val memoryPercentage: Float = 0f,
    val diskPercentage: Float = 0f,
    val playerOnline: Int = 0,
    val playerMax: Int = 0,
    val cachedAt: Long = System.currentTimeMillis()
)

@Dao
interface CachedServerDao {
    @Query("SELECT * FROM cached_servers ORDER BY name ASC")
    suspend fun getAll(): List<CachedServer>

    @Query("SELECT * FROM cached_servers WHERE id = :id")
    suspend fun getById(id: String): CachedServer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(servers: List<CachedServer>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(server: CachedServer)

    @Query("DELETE FROM cached_servers")
    suspend fun deleteAll()

    @Query("DELETE FROM cached_servers WHERE cachedAt < :timestamp")
    suspend fun deleteStale(timestamp: Long)
}
