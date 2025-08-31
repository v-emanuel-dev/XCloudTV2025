package com.ivip.xcloudtv2025.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivip.xcloudtv2025.data.local.preferences.PreferencesManager
import com.ivip.xcloudtv2025.data.repository.ChannelRepository
import com.ivip.xcloudtv2025.domain.model.Channel
import com.ivip.xcloudtv2025.domain.usecase.UpdatePlaylistUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel principal do aplicativo (com Koin)
 */
class MainViewModel(
    private val channelRepository: ChannelRepository,
    private val preferencesManager: PreferencesManager,
    private val updatePlaylistUseCase: UpdatePlaylistUseCase
) : ViewModel() {

    // Estados da UI
    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    // Dados dos canais
    val channels = channelRepository.getAllChannels()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Canal atual
    private val _currentChannel = MutableStateFlow<Channel?>(null)
    val currentChannel = _currentChannel.asStateFlow()

    // Estado do player
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState = _playerState.asStateFlow()

    init {
        // Inicializar canais padrão automaticamente
        initializeApp()

        // Observar mudanças nos canais e selecionar o primeiro automaticamente
        viewModelScope.launch {
            channels.collect { channelList ->
                if (channelList.isNotEmpty() && _currentChannel.value == null) {
                    // Seleciona o primeiro canal automaticamente
                    selectChannel(channelList.first())
                }
            }
        }
    }

    /**
     * Inicializa o aplicativo
     */
    fun initializeApp() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                // Verifica se há canais no banco
                val hasChannels = channelRepository.hasChannels()

                if (!hasChannels) {
                    // Inicializa com canais padrão se não houver nenhum
                    updatePlaylistUseCase.initializeDefaultChannels()
                    android.util.Log.d("MainViewModel", "Canais padrão inicializados")
                } else {
                    android.util.Log.d("MainViewModel", "Canais já existem no banco")
                }

                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Erro ao inicializar app", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Erro ao carregar canais: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Seleciona um canal para reprodução
     */
    fun selectChannel(channel: Channel) {
        viewModelScope.launch {
            try {
                android.util.Log.d("MainViewModel", "Selecionando canal: ${channel.name} - URL: ${channel.url}")

                _playerState.update { it.copy(isLoading = true, error = null) }
                _currentChannel.value = channel

                // Marca como assistido
                channelRepository.markChannelAsWatched(channel.id)

                _playerState.update {
                    it.copy(
                        isLoading = false,
                        isPlaying = true,
                        error = null
                    )
                }

                android.util.Log.d("MainViewModel", "Canal selecionado com sucesso")

            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Erro ao selecionar canal", e)
                _playerState.update {
                    it.copy(
                        isLoading = false,
                        isPlaying = false,
                        error = "Erro ao carregar canal: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Atualiza playlist a partir de URL
     */
    fun updatePlaylist(url: String, username: String, password: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("MainViewModel", "Atualizando playlist: $url")

                _uiState.update { it.copy(isLoading = true) }
                val result = updatePlaylistUseCase.fromUrl(url, username, password)

                if (result.isSuccess) {
                    val updateResult = result.getOrNull()!!

                    android.util.Log.d("MainViewModel", "Playlist atualizada: ${updateResult.totalChannels} canais")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            message = "${updateResult.totalChannels} canais carregados com sucesso",
                            showSettings = false
                        )
                    }

                    // Salvar configurações da playlist
                    preferencesManager.setPlaylistUrl(url)
                    if (username.isNotBlank() && password.isNotBlank()) {
                        preferencesManager.setCredentials(username, password)
                        preferencesManager.setUseAuthentication(true)
                    }
                } else {
                    val errorMessage = result.exceptionOrNull()?.message ?: "Erro ao atualizar playlist"
                    android.util.Log.e("MainViewModel", "Erro na playlist: $errorMessage")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Erro ao atualizar playlist", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Erro ao processar playlist: ${e.message}"
                    )
                }
            }
        }
    }

    /**
     * Toggle play/pause do player
     */
    fun togglePlayPause() {
        _playerState.update {
            it.copy(isPlaying = !it.isPlaying)
        }
    }

    /**
     * Para a reprodução
     */
    fun stopPlayback() {
        _playerState.update {
            it.copy(isPlaying = false)
        }
        _currentChannel.value = null
    }

    /**
     * Próximo canal
     */
    fun nextChannel() {
        viewModelScope.launch {
            _currentChannel.value?.let { current ->
                val nextChannel = channelRepository.getNextChannel(current.id)
                nextChannel?.let { selectChannel(it) }
            }
        }
    }

    /**
     * Canal anterior
     */
    fun previousChannel() {
        viewModelScope.launch {
            _currentChannel.value?.let { current ->
                val prevChannel = channelRepository.getPreviousChannel(current.id)
                prevChannel?.let { selectChannel(it) }
            }
        }
    }

    /**
     * Abre configurações
     */
    fun openSettings() {
        _uiState.update { it.copy(showSettings = true) }
    }

    /**
     * Fecha configurações
     */
    fun closeSettings() {
        _uiState.update { it.copy(showSettings = false) }
    }

    /**
     * Limpa mensagens
     */
    fun clearMessage() {
        _uiState.update { it.copy(error = null, message = null) }
    }

    /**
     * Atualiza dados (pull to refresh)
     */
    fun refreshData() {
        viewModelScope.launch {
            try {
                // Recarregar canais se necessário
                val channelCount = channelRepository.getActiveChannelsCount()
                android.util.Log.d("MainViewModel", "Dados atualizados: $channelCount canais")
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Erro ao atualizar dados", e)
            }
        }
    }

    /**
     * Salva estado atual
     */
    fun saveCurrentState() {
        viewModelScope.launch {
            try {
                _currentChannel.value?.let { channel ->
                    preferencesManager.setLastWatchedChannelId(channel.id)
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Erro ao salvar estado", e)
            }
        }
    }

    /**
     * Limpa recursos
     */
    fun cleanup() {
        stopPlayback()
    }

    // Métodos para controle do player via teclas do controle remoto
    fun handleSelectAction() {
        _currentChannel.value?.let { channel ->
            togglePlayPause()
        }
    }

    fun handleBackAction() {
        when {
            _uiState.value.showSettings -> closeSettings()
            _currentChannel.value != null -> stopPlayback()
            else -> {
                // Pode implementar lógica para sair do app
            }
        }
    }

    /**
     * Callback do player quando há erro
     */
    fun onPlayerError(error: String) {
        android.util.Log.e("MainViewModel", "Erro do player: $error")
        _playerState.update {
            it.copy(
                isLoading = false,
                isPlaying = false,
                error = error
            )
        }
    }

    /**
     * Callback do player quando está pronto
     */
    fun onPlayerReady() {
        android.util.Log.d("MainViewModel", "Player pronto para reprodução")
        _playerState.update {
            it.copy(
                isLoading = false,
                isPlaying = true,
                error = null
            )
        }
    }

    // Data classes
    data class UiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val message: String? = null,
        val showSettings: Boolean = false
    )

    data class PlayerState(
        val isPlaying: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null,
        val volume: Float = 1.0f
    )

    data class PlaylistSettings(
        val url: String,
        val username: String,
        val password: String,
        val useAuth: Boolean
    )
}