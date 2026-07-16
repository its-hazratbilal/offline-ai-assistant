package com.hazratbilal.offlineaiassistant.di

import com.hazratbilal.offlineaiassistant.data.repository.ChatRepositoryImpl
import com.hazratbilal.offlineaiassistant.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

}