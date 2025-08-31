package com.ivip.xcloudtv2025.domain.usecase

import com.ivip.xcloudtv2025.data.repository.ChannelRepository
import com.ivip.xcloudtv2025.domain.model.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case for getting channels with filtering and sorting
 */
class GetChannelsUseCase(
    private val channelRepository: ChannelRepository
) {

    /**
     * Gets all channels
     */
    fun getAllChannels(): Flow<List<Channel>> {
        return channelRepository.getAllChannels()
    }

    /**
     * Gets channels by category
     */
    fun getChannelsByCategory(category: String): Flow<List<Channel>> {
        return if (category == "Todos" || category.isBlank()) {
            channelRepository.getAllChannels()
        } else {
            channelRepository.getChannelsByCategory(category)
        }
    }

    /**
     * Gets favorite channels
     */
    fun getFavoriteChannels(): Flow<List<Channel>> {
        return channelRepository.getFavoriteChannels()
    }

    /**
     * Gets recently watched channels
     */
    fun getRecentChannels(limit: Int = 10): Flow<List<Channel>> {
        return channelRepository.getRecentlyWatchedChannels(limit)
    }

    /**
     * Search channels
     */
    fun searchChannels(query: String): Flow<List<Channel>> {
        return channelRepository.searchChannels(query)
    }

    /**
     * Get channels grouped by category
     */
    fun getChannelsGroupedByCategory(): Flow<Map<String, List<Channel>>> {
        return channelRepository.getChannelsGroupedByCategory()
    }

    /**
     * Get available categories
     */
    suspend fun getCategories(): List<String> {
        return channelRepository.getAllCategories()
    }

    /**
     * Get filtered channels based on criteria
     */
    fun getFilteredChannels(
        category: String = "Todos",
        onlyFavorites: Boolean = false,
        onlyLive: Boolean = false,
        searchQuery: String = ""
    ): Flow<List<Channel>> {
        return getAllChannels().map { channels ->
            channels.filter { channel ->
                // Category filter
                val categoryMatch = category == "Todos" || channel.category == category

                // Favorites filter
                val favoriteMatch = !onlyFavorites || channel.isFavorite

                // Live filter
                val liveMatch = !onlyLive || channel.isLive

                // Search filter
                val searchMatch = searchQuery.isBlank() ||
                        channel.name.contains(searchQuery, ignoreCase = true) ||
                        channel.description.contains(searchQuery, ignoreCase = true)

                categoryMatch && favoriteMatch && liveMatch && searchMatch
            }
        }
    }
}