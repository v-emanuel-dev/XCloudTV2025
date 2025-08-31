package com.ivip.xcloudtv2025.di

import com.ivip.xcloudtv2025.data.remote.M3UParser
import com.ivip.xcloudtv2025.data.remote.PlaylistService
import org.koin.dsl.module

/**
 * Módulo Koin para dependências de rede
 */
val networkModule = module {

    // M3U Parser
    single<M3UParser> { M3UParser() }

    // Playlist Service
    single<PlaylistService> { PlaylistService() }
}