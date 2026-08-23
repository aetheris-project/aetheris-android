package com.aetheris.android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aetheris.android.data.local.dao.CachedServerDao
import com.aetheris.android.data.local.dao.CachedServer

@Database(
    entities = [CachedServer::class],
    version = 1,
    exportSchema = false
)
abstract class AetherisDatabase : RoomDatabase() {
    abstract fun cachedServerDao(): CachedServerDao

    companion object {
        fun create(context: Context): AetherisDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AetherisDatabase::class.java,
                "aetheris.db"
            ).build()
        }
    }
}
