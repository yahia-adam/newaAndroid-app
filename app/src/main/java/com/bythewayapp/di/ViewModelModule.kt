package com.bythewayapp.di

import android.content.Context
import com.bythewayapp.core.AndroidPermissionManager
import com.bythewayapp.core.LocationManager
import com.bythewayapp.data.EventRepository
import com.bythewayapp.ui.viewModels.HomeViewModel
import com.bythewayapp.ui.viewModels.PrivyLoginViewModel
import com.bythewayapp.core.PrivyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideHomeViewModel(
        eventRepository: EventRepository,
        locationManager: LocationManager,
        context: Context
    ): HomeViewModel {
        return HomeViewModel(
            eventRepository = eventRepository,
            locationManager = locationManager,
            context = context
        )
    }

    @Provides
    fun providePrivyLoginViewModel(
        privyManager: PrivyManager,
    ): PrivyLoginViewModel {
        return PrivyLoginViewModel(
            privyManager
        )
    }
}