package com.ivip.xcloudtv2025.di

import com.ivip.xcloudtv2025.presentation.screens.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Módulo Koin para ViewModels
 */
val viewModelModule = module {

    // MainViewModel - certifique-se de que todas as dependências estão sendo passadas corretamente
    viewModel {
        android.util.Log.d("Koin", "Criando MainViewModel...")
        MainViewModel(
            channelRepository = get(),
            preferencesManager = get(),
            updatePlaylistUseCase = get()
        )
    }
}