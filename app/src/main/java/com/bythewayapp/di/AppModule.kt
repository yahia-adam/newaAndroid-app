package com.bythewayapp.di

import android.app.Application
import android.content.Context
import com.bythewayapp.ByTheWayApplication
import com.bythewayapp.utils.ConnectionStateManager
import com.bythewayapp.utils.PrivyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApp(@ApplicationContext application: Context): ByTheWayApplication {
        return application as ByTheWayApplication
    }

    @Provides
    @Singleton
    fun provideAppContext(application: Application): Context = application

    @Provides
    @Singleton
    fun providePrivyManager(
        @ApplicationContext context: Context,
        connectionStateManager: ConnectionStateManager
    ): PrivyManager {
        return PrivyManager(context, connectionStateManager)
    }

    @Provides
    @Singleton
    fun provideConnectionStateManager(
        @ApplicationContext context: Context,
    ): ConnectionStateManager {
        return ConnectionStateManager(context)
    }
}
