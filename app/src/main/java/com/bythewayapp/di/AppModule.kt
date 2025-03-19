package com.bythewayapp.di

import android.app.Application
import android.content.Context
import com.bythewayapp.ByTheWayApplication
import com.bythewayapp.core.AndroidPermissionManager
import com.bythewayapp.core.ConnectionStateManager
import com.bythewayapp.core.ErrorHandler
import com.bythewayapp.core.EventsFileLoader
import com.bythewayapp.core.LocationManager
import com.bythewayapp.core.PermissionManager
import com.bythewayapp.core.PrivyManager
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
        errorHandler: ErrorHandler
    ): PrivyManager {
        return PrivyManager(context, errorHandler)
    }

    @Provides
    @Singleton
    fun provideConnectionStateManager(
        @ApplicationContext context: Context,
    ): ConnectionStateManager {
        return ConnectionStateManager(context)
    }

    @Provides
    @Singleton
    fun provideEventFileLoader(
        @ApplicationContext context: Context,
    ): EventsFileLoader {
        return EventsFileLoader(context)
    }

    @Provides
    @Singleton
    fun ProvideAndroidPermissionManager(
        @ApplicationContext context: Context,
    ): PermissionManager {
        return AndroidPermissionManager(context)
    }

    @Provides
    @Singleton
    fun ProvideLocationManager(
        @ApplicationContext context: Context,
        permissionManager: PermissionManager
    ): LocationManager {  // Change return type to PermissionManager
        return LocationManager(context, permissionManager)
    }

    @Provides
    @Singleton
    fun ProvidePrivyErrorHandler(
        @ApplicationContext context: Context,
    ): ErrorHandler {  // Change return type to PermissionManager
        return ErrorHandler(context)
    }

}
