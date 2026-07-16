package com.hazratbilal.offlineaiassistant.di

import android.content.Context
import androidx.room.Room
import com.hazratbilal.offlineaiassistant.data.local.dao.ChatDao
import com.hazratbilal.offlineaiassistant.data.local.dao.ChatSessionDao
import com.hazratbilal.offlineaiassistant.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "offline_ai_assistant.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideChatDao(database: AppDatabase): ChatDao = database.chatDao()

    @Provides
    @Singleton
    fun provideChatSessionDao(database: AppDatabase): ChatSessionDao = database.chatSessionDao()
}