package com.bythewayapp.di

import android.content.Context
import com.bythewayapp.data.EventRepository
import com.bythewayapp.ui.viewModels.HomeViewModel
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
}