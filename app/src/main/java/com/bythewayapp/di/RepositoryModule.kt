package com.bythewayapp.di

import com.bythewayapp.core.EventsFileLoader
import com.bythewayapp.data.EventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideEventRepository(
        fileLoader: EventsFileLoader
    ): EventRepository {
        return EventRepository(fileLoader)
    }
}