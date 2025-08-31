package com.ivip.xcloudtv2025.data.repository

import com.ivip.xcloudtv2025.data.local.database.ChannelDao
import com.ivip.xcloudtv2025.data.local.database.toDomain
import com.ivip.xcloudtv2025.data.local.database.toEntity
import com.ivip.xcloudtv2025.data.local.preferences.PreferencesManager
import com.ivip.xcloudtv2025.data.remote.M3UParser
import com.ivip.xcloudtv2025.data.remote.PlaylistService
import com.ivip.xcloudtv2025.domain.model.Channel
import com.ivip.xcloudtv2025.domain.model.DefaultChannels
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

/**
 * Repositório responsável por gerenciar os dados dos canais
 * Combina dados locais (Room) com dados remotos (M3U playlists)
 */
class ChannelRepository(
    private val channelDao: ChannelDao,
    private val preferencesManager: PreferencesManager,
    private val playlistService: PlaylistService,
    private val m3uParser: M3UParser
) {

    /**
     * Obtém todos os canais como Flow (atualizações em tempo real)
     */
    fun getAllChannels(): Flow<List<Channel>> {
        return channelDao.getAllChannels()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
    }

    /**
     * Obtém canais por categoria
     */
    fun getChannelsByCategory(category: String): Flow<List<Channel>> {
        return channelDao.getChannelsByCategory(category)
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
    }

    /**
     * Obtém canais favoritos
     */
    fun getFavoriteChannels(): Flow<List<Channel>> {
        return channelDao.getFavoriteChannels()
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
    }

    /**
     * Obtém canais assistidos recentemente
     */
    fun getRecentlyWatchedChannels(limit: Int = 10): Flow<List<Channel>> {
        return channelDao.getRecentlyWatchedChannels(limit)
            .map { entities -> entities.map { it.toDomain() } }
            .distinctUntilChanged()
    }

    /**
     * Busca canais por nome ou descrição
     */
    fun searchChannels(query: String): Flow<List<Channel>> {
        return if (query.isBlank()) {
            flowOf(emptyList())
        } else {
            channelDao.searchChannels(query)
                .map { entities -> entities.map { it.toDomain() } }
                .distinctUntilChanged()
        }
    }

    /**
     * Obtém um canal específico por ID
     */
    suspend fun getChannelById(id: Long): Channel? {
        return withContext(Dispatchers.IO) {
            channelDao.getChannelById(id)?.toDomain()
        }
    }

    /**
     * Obtém todas as categorias disponíveis
     */
    suspend fun getAllCategories(): List<String> {
        return withContext(Dispatchers.IO) {
            channelDao.getAllCategories()
        }
    }

    /**
     * Insere um novo canal
     */
    suspend fun insertChannel(channel: Channel): Long {
        return withContext(Dispatchers.IO) {
            channelDao.insertChannel(channel.toEntity())
        }
    }

    /**
     * Insere múltiplos canais
     */
    suspend fun insertChannels(channels: List<Channel>) {
        withContext(Dispatchers.IO) {
            channelDao.insertChannels(channels.map { it.toEntity() })
        }
    }

    /**
     * Atualiza um canal
     */
    suspend fun updateChannel(channel: Channel) {
        withContext(Dispatchers.IO) {
            channelDao.updateChannel(channel.toEntity())
        }
    }

    /**
     * Marca/desmarca canal como favorito
     */
    suspend fun toggleFavorite(channelId: Long, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            channelDao.toggleFavorite(channelId, isFavorite)
        }
    }

    /**
     * Atualiza o timestamp da última visualização
     */
    suspend fun markChannelAsWatched(channelId: Long) {
        withContext(Dispatchers.IO) {
            channelDao.updateLastWatched(channelId)
            // Também salva nas preferências como último canal assistido
            preferencesManager.setLastWatchedChannelId(channelId)
        }
    }

    /**
     * Remove um canal
     */
    suspend fun deleteChannel(channel: Channel) {
        withContext(Dispatchers.IO) {
            channelDao.deleteChannel(channel.toEntity())
        }
    }

    /**
     * Remove todos os canais (limpa a playlist)
     */
    suspend fun deleteAllChannels() {
        withContext(Dispatchers.IO) {
            channelDao.deleteAllChannels()
        }
    }

    /**
     * Obtém contagem de canais ativos
     */
    suspend fun getActiveChannelsCount(): Int {
        return withContext(Dispatchers.IO) {
            channelDao.getActiveChannelsCount()
        }
    }

    /**
     * Verifica se há canais salvos no banco
     */
    suspend fun hasChannels(): Boolean {
        return getActiveChannelsCount() > 0
    }

    /**
     * Inicializa o banco com canais padrão se estiver vazio
     */
    suspend fun initializeDefaultChannelsIfNeeded() {
        withContext(Dispatchers.IO) {
            if (!hasChannels()) {
                insertChannels(DefaultChannels.DEFAULT_FREE_CHANNELS)
            }
        }
    }

    /**
     * Atualiza a playlist a partir de uma URL M3U
     */
    suspend fun updatePlaylistFromUrl(
        url: String,
        username: String = "",
        password: String = "",
        replaceExisting: Boolean = true
    ): Result<PlaylistUpdateResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Baixa o conteúdo da playlist
                val playlistResult = playlistService.fetchPlaylist(url, username, password)
                if (playlistResult.isFailure) {
                    return@withContext Result.failure(
                        playlistResult.exceptionOrNull() ?: Exception("Erro ao baixar playlist")
                    )
                }

                val m3uContent = playlistResult.getOrNull() ?: ""

                // Faz o parse do conteúdo M3U
                val parseResult = m3uParser.parseM3U(m3uContent)
                if (parseResult.isFailure) {
                    return@withContext Result.failure(
                        parseResult.exceptionOrNull() ?: Exception("Erro ao processar playlist")
                    )
                }

                val newChannels = parseResult.getOrNull() ?: emptyList()

                if (newChannels.isEmpty()) {
                    return@withContext Result.failure(
                        Exception("Nenhum canal encontrado na playlist")
                    )
                }

                // Se deve substituir canais existentes, limpa o banco primeiro
                if (replaceExisting) {
                    deleteAllChannels()
                }

                // Insere os novos canais
                insertChannels(newChannels)

                // Obtém estatísticas da atualização
                val stats = m3uParser.getPlaylistStats(m3uContent)

                val result = PlaylistUpdateResult(
                    success = true,
                    totalChannels = newChannels.size,
                    newChannels = if (replaceExisting) newChannels.size else newChannels.size,
                    updatedChannels = if (replaceExisting) 0 else 0, // TODO: implementar lógica de atualização
                    categoriesCount = stats.categories.size,
                    message = "Playlist atualizada com sucesso: ${stats.getSummary()}"
                )

                Result.success(result)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Atualiza playlist a partir de conteúdo M3U direto
     */
    suspend fun updatePlaylistFromContent(
        m3uContent: String,
        replaceExisting: Boolean = true
    ): Result<PlaylistUpdateResult> {
        return withContext(Dispatchers.IO) {
            try {
                // Valida se é um M3U válido
                if (!m3uParser.isValidM3U(m3uContent)) {
                    return@withContext Result.failure(
                        Exception("Conteúdo M3U inválido")
                    )
                }

                // Faz o parse do conteúdo
                val parseResult = m3uParser.parseM3U(m3uContent)
                if (parseResult.isFailure) {
                    return@withContext Result.failure(
                        parseResult.exceptionOrNull() ?: Exception("Erro ao processar playlist")
                    )
                }

                val newChannels = parseResult.getOrNull() ?: emptyList()

                if (newChannels.isEmpty()) {
                    return@withContext Result.failure(
                        Exception("Nenhum canal encontrado na playlist")
                    )
                }

                // Se deve substituir canais existentes, limpa o banco primeiro
                if (replaceExisting) {
                    deleteAllChannels()
                }

                // Insere os novos canais
                insertChannels(newChannels)

                // Obtém estatísticas da atualização
                val stats = m3uParser.getPlaylistStats(m3uContent)

                val result = PlaylistUpdateResult(
                    success = true,
                    totalChannels = newChannels.size,
                    newChannels = if (replaceExisting) newChannels.size else newChannels.size,
                    updatedChannels = 0,
                    categoriesCount = stats.categories.size,
                    message = "Playlist atualizada: ${stats.getSummary()}"
                )

                Result.success(result)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtém canais agrupados por categoria para exibição
     */
    fun getChannelsGroupedByCategory(): Flow<Map<String, List<Channel>>> {
        return getAllChannels()
            .map { channels ->
                channels.groupBy { it.category }
                    .toSortedMap() // Ordena as categorias alfabeticamente
            }
            .distinctUntilChanged()
    }

    /**
     * Obtém canal seguinte na lista (para auto-play)
     */
    suspend fun getNextChannel(currentChannelId: Long, category: String? = null): Channel? {
        return withContext(Dispatchers.IO) {
            val channels = if (category != null) {
                channelDao.getChannelsByCategory(category).first().map { it.toDomain() }
            } else {
                channelDao.getAllChannels().first().map { it.toDomain() }
            }

            val currentIndex = channels.indexOfFirst { it.id == currentChannelId }
            if (currentIndex != -1 && currentIndex < channels.size - 1) {
                channels[currentIndex + 1]
            } else {
                // Se chegou ao final, volta para o primeiro
                channels.firstOrNull()
            }
        }
    }

    /**
     * Obtém canal anterior na lista
     */
    suspend fun getPreviousChannel(currentChannelId: Long, category: String? = null): Channel? {
        return withContext(Dispatchers.IO) {
            val channels = if (category != null) {
                channelDao.getChannelsByCategory(category).first().map { it.toDomain() }
            } else {
                channelDao.getAllChannels().first().map { it.toDomain() }
            }

            val currentIndex = channels.indexOfFirst { it.id == currentChannelId }
            if (currentIndex > 0) {
                channels[currentIndex - 1]
            } else {
                // Se está no primeiro, vai para o último
                channels.lastOrNull()
            }
        }
    }

    /**
     * Classe para resultado da atualização de playlist
     */
    data class PlaylistUpdateResult(
        val success: Boolean,
        val totalChannels: Int = 0,
        val newChannels: Int = 0,
        val updatedChannels: Int = 0,
        val categoriesCount: Int = 0,
        val message: String = "",
        val error: String? = null
    ) {
        val hasNewContent: Boolean
            get() = newChannels > 0 || updatedChannels > 0
    }
}