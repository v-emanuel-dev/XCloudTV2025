package com.ivip.xcloudtv2025.data.local.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operações com canais no banco de dados
 * CORRIGIDO: Ordenação por ID quando sort_order for igual
 */
@Dao
interface ChannelDao {

    /**
     * Obtém todos os canais ativos ordenados por sort_order, depois por ID
     * MUDANÇA: Adicionado ID na ordenação para garantir ordem consistente
     */
    @Query("SELECT * FROM channels WHERE is_active = 1 ORDER BY sort_order ASC, id ASC")
    fun getAllChannels(): Flow<List<ChannelEntity>>

    /**
     * Obtém canais por categoria
     */
    @Query("SELECT * FROM channels WHERE category = :category AND is_active = 1 ORDER BY sort_order ASC, id ASC")
    fun getChannelsByCategory(category: String): Flow<List<ChannelEntity>>

    /**
     * Obtém canais favoritos
     */
    @Query("SELECT * FROM channels WHERE is_favorite = 1 AND is_active = 1 ORDER BY last_watched DESC, sort_order ASC, id ASC")
    fun getFavoriteChannels(): Flow<List<ChannelEntity>>

    /**
     * Obtém canais assistidos recentemente
     */
    @Query("SELECT * FROM channels WHERE last_watched > 0 AND is_active = 1 ORDER BY last_watched DESC LIMIT :limit")
    fun getRecentlyWatchedChannels(limit: Int = 10): Flow<List<ChannelEntity>>

    /**
     * Busca canais por nome ou descrição
     */
    @Query("""
        SELECT * FROM channels 
        WHERE (name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') 
        AND is_active = 1 
        ORDER BY 
            CASE WHEN name LIKE :query || '%' THEN 1 ELSE 2 END,
            sort_order ASC, id ASC
    """)
    fun searchChannels(query: String): Flow<List<ChannelEntity>>

    /**
     * Obtém um canal específico por ID
     */
    @Query("SELECT * FROM channels WHERE id = :id")
    suspend fun getChannelById(id: Long): ChannelEntity?

    /**
     * Obtém todas as categorias distintas
     */
    @Query("SELECT DISTINCT category FROM channels WHERE is_active = 1 ORDER BY category ASC")
    suspend fun getAllCategories(): List<String>

    /**
     * Insere um novo canal
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: ChannelEntity): Long

    /**
     * Insere múltiplos canais
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<ChannelEntity>)

    /**
     * Atualiza um canal existente
     */
    @Update
    suspend fun updateChannel(channel: ChannelEntity)

    /**
     * Atualiza múltiplos canais
     */
    @Update
    suspend fun updateChannels(channels: List<ChannelEntity>)

    /**
     * Deleta um canal (marca como inativo)
     */
    @Query("UPDATE channels SET is_active = 0 WHERE id = :id")
    suspend fun deactivateChannel(id: Long)

    /**
     * Remove fisicamente um canal do banco
     */
    @Delete
    suspend fun deleteChannel(channel: ChannelEntity)

    /**
     * Remove todos os canais (limpa a playlist)
     */
    @Query("DELETE FROM channels")
    suspend fun deleteAllChannels()

    /**
     * Marca/desmarca canal como favorito
     */
    @Query("UPDATE channels SET is_favorite = :isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: Long, isFavorite: Boolean)

    /**
     * Atualiza o timestamp da última visualização
     */
    @Query("UPDATE channels SET last_watched = :timestamp WHERE id = :id")
    suspend fun updateLastWatched(id: Long, timestamp: Long = System.currentTimeMillis())

    /**
     * Conta total de canais ativos
     */
    @Query("SELECT COUNT(*) FROM channels WHERE is_active = 1")
    suspend fun getActiveChannelsCount(): Int

    /**
     * Verifica se existe canal com a URL específica
     */
    @Query("SELECT COUNT(*) FROM channels WHERE url = :url AND is_active = 1")
    suspend fun countChannelsWithUrl(url: String): Int

    /**
     * NOVA: Atualiza ordem de um canal específico
     */
    @Query("UPDATE channels SET sort_order = :sortOrder WHERE id = :id")
    suspend fun updateChannelSortOrder(id: Long, sortOrder: Int)

    /**
     * NOVA: Reordena todos os canais por categoria
     */
    @Query("SELECT * FROM channels WHERE is_active = 1 ORDER BY category ASC, sort_order ASC, id ASC")
    suspend fun getAllChannelsForReordering(): List<ChannelEntity>
}