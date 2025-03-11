package com.bythewayapp.di

import android.content.Context
import com.bythewayapp.data.EventRepository
import com.bythewayapp.ui.viewModels.HomeViewModel
import com.bythewayapp.ui.viewModels.PrivyLoginViewModel
import com.bythewayapp.utils.ConnectionStateManager
import com.bythewayapp.utils.PrivyManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.lifecycle.HiltViewModel

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun provideHomeViewModel(
        eventRepository: EventRepository,
        context: Context
    ): HomeViewModel {
        return HomeViewModel(
            eventRepository = eventRepository,
            context = context
        )
    }

    @Provides
    fun providePrivyLoginViewModel(
        context: Context,
        privyManager: PrivyManager,
         connectivityManager: ConnectionStateManager
    ): PrivyLoginViewModel {
        return PrivyLoginViewModel(
            context,
            privyManager,
            connectivityManager
        )
    }
}