package com.ivip.xcloudtv2025.domain.usecase

import com.ivip.xcloudtv2025.data.repository.ChannelRepository

/**
 * Use case for updating playlists
 */
class UpdatePlaylistUseCase(
    private val channelRepository: ChannelRepository
) {

    /**
     * Updates playlist from URL
     */
    suspend fun fromUrl(
        url: String,
        username: String = "",
        password: String = "",
        replaceExisting: Boolean = true
    ): Result<ChannelRepository.PlaylistUpdateResult> {
        return channelRepository.updatePlaylistFromUrl(url, username, password, replaceExisting)
    }

    /**
     * Updates playlist from M3U content
     */
    suspend fun fromContent(
        content: String,
        replaceExisting: Boolean = true
    ): Result<ChannelRepository.PlaylistUpdateResult> {
        return channelRepository.updatePlaylistFromContent(content, replaceExisting)
    }

    /**
     * Initializes default channels if database is empty
     */
    suspend fun initializeDefaultChannels() {
        channelRepository.initializeDefaultChannelsIfNeeded()
    }

    /**
     * Clears all channels
     */
    suspend fun clearAllChannels() {
        channelRepository.deleteAllChannels()
    }
}