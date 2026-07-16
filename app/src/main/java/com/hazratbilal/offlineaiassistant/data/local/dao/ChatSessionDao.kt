package com.hazratbilal.offlineaiassistant.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hazratbilal.offlineaiassistant.data.local.entity.ChatSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatSessionDao {

    @Insert
    suspend fun insert(session: ChatSessionEntity): Long

    @Query("SELECT * FROM chat_sessions ORDER BY updatedAt DESC")
    fun getAllSessions(): Flow<List<ChatSessionEntity>>

    @Query("SELECT * FROM chat_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): ChatSessionEntity?

    @Query("UPDATE chat_sessions SET label = :label WHERE id = :sessionId")
    suspend fun updateLabel(sessionId: Long, label: String)

    @Query("UPDATE chat_sessions SET title = :title, updatedAt = :updatedAt WHERE id = :sessionId")
    suspend fun updateTitleAndTimestamp(sessionId: Long, title: String, updatedAt: Long)

    @Query("DELETE FROM chat_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)
}