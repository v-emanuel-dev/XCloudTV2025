package com.ivip.xcloudtv2025.di

import com.ivip.xcloudtv2025.data.repository.ChannelRepository
import com.ivip.xcloudtv2025.domain.usecase.GetChannelsUseCase
import com.ivip.xcloudtv2025.domain.usecase.UpdatePlaylistUseCase
import org.koin.dsl.module

/**
 * Módulo Koin para repositórios e use cases
 */
val repositoryModule = module {

    // Channel Repository
    single<ChannelRepository> {
        ChannelRepository(
            channelDao = get(),
            preferencesManager = get(),
            playlistService = get(),
            m3uParser = get()
        )
    }

    // Use Cases
    single<GetChannelsUseCase> { GetChannelsUseCase(get()) }
    single<UpdatePlaylistUseCase> { UpdatePlaylistUseCase(get()) }
}