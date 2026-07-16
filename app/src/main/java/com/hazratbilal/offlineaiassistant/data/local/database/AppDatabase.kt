package com.hazratbilal.offlineaiassistant.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hazratbilal.offlineaiassistant.data.local.dao.ChatDao
import com.hazratbilal.offlineaiassistant.data.local.dao.ChatSessionDao
import com.hazratbilal.offlineaiassistant.data.local.entity.ChatEntity
import com.hazratbilal.offlineaiassistant.data.local.entity.ChatSessionEntity

@Database(entities = [ChatEntity::class, ChatSessionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun chatSessionDao(): ChatSessionDao
}