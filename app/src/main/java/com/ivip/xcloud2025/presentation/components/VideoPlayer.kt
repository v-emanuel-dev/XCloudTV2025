package com.ivip.xcloudtv2025.presentation.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.*
import androidx.media3.exoplayer.*
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.common.util.Util
import com.ivip.xcloudtv2025.domain.model.Channel
import kotlinx.coroutines.delay

/**
 * Componente de reprodução de vídeo usando Media3
 * Otimizado para streams IPTV/HLS e Android TV
 */
@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    channel: Channel,
    isPlaying: Boolean = true,
    isLoading: Boolean = false,
    volume: Float = 1.0f,
    onPlayerReady: () -> Unit = {},
    onPlayerError: (String) -> Unit = {},
    onPlaybackStateChanged: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var playerError by remember { mutableStateOf<String?>(null) }
    var isBuffering by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0L) }
    var duration by remember { mutableStateOf(0L) }

    // Controla se os controles estão visíveis
    var showControls by remember { mutableStateOf(false) }

    // Efeito para criar o player quando o componente é montado
    LaunchedEffect(Unit) {
        try {
            exoPlayer = createExoPlayer(context)
            exoPlayer?.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    val errorMessage = when (error.errorCode) {
                        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ->
                            "Erro de conexão de rede"
                        PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ->
                            "Timeout na conexão"
                        PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED ->
                            "Formato de stream inválido"
                        else -> "Erro na reprodução: ${error.message}"
                    }
                    playerError = errorMessage
                    onPlayerError(errorMessage)
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    isBuffering = playbackState == Player.STATE_BUFFERING
                    when (playbackState) {
                        Player.STATE_READY -> onPlayerReady()
                        Player.STATE_ENDED -> onPlaybackStateChanged(false)
                    }
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    onPlaybackStateChanged(playing)
                }
            })
        } catch (e: Exception) {
            playerError = "Erro ao inicializar player: ${e.message}"
            onPlayerError(playerError!!)
        }
    }

    // Efeito para carregar o canal quando a URL muda
    LaunchedEffect(channel.url) {
        exoPlayer?.let { player ->
            try {
                val mediaSource = createMediaSource(context, channel)
                player.setMediaSource(mediaSource)
                player.prepare()
                player.playWhenReady = true
                playerError = null
            } catch (e: Exception) {
                playerError = "Erro ao carregar canal: ${e.message}"
                onPlayerError(playerError!!)
            }
        }
    }

    // Controla play/pause
    LaunchedEffect(isPlaying) {
        exoPlayer?.let { player ->
            player.playWhenReady = isPlaying
        }
    }

    // Controla volume
    LaunchedEffect(volume) {
        exoPlayer?.volume = volume
    }

    // Atualiza posição periodicamente
    LaunchedEffect(exoPlayer) {
        while (exoPlayer != null) {
            exoPlayer?.let { player ->
                currentPosition = player.currentPosition
                duration = if (player.duration > 0) player.duration else 0L
            }
            delay(1000)
        }
    }

    // Limpa recursos quando sai da composição
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    Box(
        modifier = modifier.background(Color.Black)
    ) {
        // Player View
        if (playerError == null) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        player = exoPlayer
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    view.player = exoPlayer
                }
            )
        } else {
            // Tela de erro
            ErrorScreen(
                error = playerError!!,
                onRetry = {
                    playerError = null
                    // Recarregar o canal
                    exoPlayer?.let { player ->
                        try {
                            val mediaSource = createMediaSource(context, channel)
                            player.setMediaSource(mediaSource)
                            player.prepare()
                            player.playWhenReady = true
                        } catch (e: Exception) {
                            playerError = "Erro ao recarregar: ${e.message}"
                        }
                    }
                }
            )
        }

        // Loading overlay
        if (isLoading || isBuffering) {
            LoadingOverlay()
        }

        // Channel info overlay (canto superior)
        ChannelInfoOverlay(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            channel = channel
        )

        // Controles customizados (aparecem quando necessário)
        if (showControls && playerError == null) {
            PlayerControls(
                modifier = Modifier.align(Alignment.BottomCenter),
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                volume = volume,
                onPlayPause = { onPlaybackStateChanged(!isPlaying) },
                onSeek = { position ->
                    exoPlayer?.seekTo(position)
                },
                onVolumeChange = { newVolume ->
                    exoPlayer?.volume = newVolume
                }
            )
        }
    }

    // Auto-hide dos controles
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(5000)
            showControls = false
        }
    }
}

/**
 * Cria uma instância do ExoPlayer configurada
 */
private fun createExoPlayer(context: Context): ExoPlayer {
    return ExoPlayer.Builder(context)
        .setLoadControl(
            DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    15000, // min buffer
                    30000, // max buffer
                    1500,  // buffer for playback
                    3000   // buffer for playback after rebuffer
                )
                .build()
        )
        .build()
}

/**
 * Cria MediaSource baseado no tipo de stream
 */
private fun createMediaSource(context: Context, channel: Channel): MediaSource {
    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setUserAgent(Util.getUserAgent(context, "XcloudTV"))
        .setConnectTimeoutMs(30000)
        .setReadTimeoutMs(30000)
        .setAllowCrossProtocolRedirects(true)

    val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

    val mediaItem = MediaItem.Builder()
        .setUri(channel.url)
        .build()

    return if (channel.url.contains(".m3u8") || channel.isLive) {
        // Stream HLS
        HlsMediaSource.Factory(dataSourceFactory)
            .setAllowChunklessPreparation(true)
            .createMediaSource(mediaItem)
    } else {
        // Stream progressivo (MP4, etc)
        ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)
    }
}

/**
 * Controles customizados do player
 */
@Composable
private fun PlayerControls(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    volume: Float,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    onVolumeChange: (Float) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Barra de progresso (apenas para VOD)
            if (duration > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(currentPosition),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Slider(
                        value = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                        onValueChange = { progress ->
                            onSeek((progress * duration).toLong())
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Text(
                        text = formatTime(duration),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Controles principais
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play/Pause
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pausar" else "Reproduzir",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Volume
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Volume",
                        tint = Color.White
                    )

                    Slider(
                        value = volume,
                        onValueChange = onVolumeChange,
                        modifier = Modifier.width(100.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                }
            }
        }
    }
}

/**
 * Overlay com informações do canal
 */
@Composable
private fun ChannelInfoOverlay(
    modifier: Modifier = Modifier,
    channel: Channel
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status live
            if (channel.isLive) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.error)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }

            Column {
                Text(
                    text = channel.displayName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall
                )

                if (channel.category.isNotBlank()) {
                    Text(
                        text = channel.category,
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

/**
 * Overlay de loading
 */
@Composable
private fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )

            Text(
                text = "Carregando...",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

/**
 * Tela de erro
 */
@Composable
private fun ErrorScreen(
    error: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.ErrorOutline,
                    contentDescription = "Erro",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = "Erro na Reprodução",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Button(
                    onClick = onRetry,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Tentar Novamente",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

/**
 * Função utilitária para formatar tempo
 */
private fun formatTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}