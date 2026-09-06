package com.pinwave.di

import android.content.Context
import androidx.room.Room
import com.pinwave.data.ai.AiAnalyzer
import com.pinwave.data.ai.MockAiAnalyzer
import com.pinwave.data.billing.BillingRepository
import com.pinwave.data.billing.MockBillingRepository
import com.pinwave.data.database.PinwaveDatabase
import com.pinwave.data.pinterest.MockPinterestRepository
import com.pinwave.data.pinterest.PinterestRepository
import com.pinwave.security.SessionTokenStore
import com.pinwave.security.TokenStore
import dagger.Binds
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
    fun provideDatabase(@ApplicationContext context: Context): PinwaveDatabase =
        Room.databaseBuilder(context, PinwaveDatabase::class.java, "pinwave.db").build()

    @Provides fun provideSavedTrendDao(db: PinwaveDatabase) = db.savedTrendDao()
    @Provides fun provideCollectionDao(db: PinwaveDatabase) = db.collectionDao()
    @Provides fun provideRecentSearchDao(db: PinwaveDatabase) = db.recentSearchDao()
}

/**
 * Demo bindings while BuildConfig.USE_MOCK_DATA is true. Roadmap steps 4–10
 * swap these for the live implementations without touching the UI layer.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPinterestRepository(impl: MockPinterestRepository): PinterestRepository

    @Binds
    @Singleton
    abstract fun bindAiAnalyzer(impl: MockAiAnalyzer): AiAnalyzer

    @Binds
    @Singleton
    abstract fun bindBillingRepository(impl: MockBillingRepository): BillingRepository

    @Binds
    @Singleton
    abstract fun bindTokenStore(impl: SessionTokenStore): TokenStore
}
