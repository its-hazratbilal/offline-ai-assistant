package com.hazratbilal.offlineaiassistant.di

import android.content.Context
import com.hazratbilal.offlineaiassistant.ai.engine.LlamaCppEngine
import com.hazratbilal.offlineaiassistant.ai.engine.LlmEngine
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EngineModule {

    @Provides
    @Singleton
    fun provideLlmEngine(
        @ApplicationContext context: Context
    ): LlmEngine {
        return LlamaCppEngine.getInstance(context)
    }
}