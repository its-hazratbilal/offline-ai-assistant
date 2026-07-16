package com.hazratbilal.offlineaiassistant.di

import com.hazratbilal.offlineaiassistant.data.local.datasource.LlmLocalDataSource
import com.hazratbilal.offlineaiassistant.data.local.datasource.LlmLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindLlmLocalDataSource(impl: LlmLocalDataSourceImpl): LlmLocalDataSource

}