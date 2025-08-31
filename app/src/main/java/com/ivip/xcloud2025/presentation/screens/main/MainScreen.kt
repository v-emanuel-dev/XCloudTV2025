package com.ivip.xcloudtv2025.presentation.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ivip.xcloudtv2025.domain.model.Channel
import com.ivip.xcloudtv2025.presentation.components.VideoPlayer
import com.ivip.xcloudtv2025.presentation.screens.settings.SettingsDialog
import com.ivip.xcloudtv2025.presentation.theme.XcloudTVTheme
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val currentChannel by viewModel.currentChannel.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    XcloudTVTheme {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (uiState.isLoading) {
                    LoadingScreen()
                } else {
                    MainContent(
                        channels = channels,
                        currentChannel = currentChannel,
                        playerState = playerState,
                        onChannelClick = { viewModel.selectChannel(it) },
                        onSettingsClick = { viewModel.openSettings() }
                    )
                }

                uiState.error?.let { error ->
                    LaunchedEffect(error) {
                        delay(5000)
                        viewModel.clearMessage()
                    }
                    ErrorSnackbar(
                        error = error,
                        onDismiss = { viewModel.clearMessage() }
                    )
                }

                uiState.message?.let { message ->
                    LaunchedEffect(message) {
                        delay(3000)
                        viewModel.clearMessage()
                    }
                    SuccessSnackbar(
                        message = message,
                        onDismiss = { viewModel.clearMessage() }
                    )
                }

                if (uiState.showSettings) {
                    SettingsDialog(
                        currentSettings = MainViewModel.PlaylistSettings(
                            url = "",
                            username = "",
                            password = "",
                            useAuth = false,
                            serverHost = "",
                            serverPort = "8080",
                            useXtreamMode = false
                        ),
                        onUpdatePlaylist = { url, username, password ->
                            viewModel.updatePlaylist(url, username, password)
                        },
                        onDismiss = { viewModel.closeSettings() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Carregando Xcloud TV...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun MainContent(
    channels: List<Channel>,
    currentChannel: Channel?,
    playerState: MainViewModel.PlayerState,
    onChannelClick: (Channel) -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        ChannelList(
            channels = channels,
            currentChannel = currentChannel,
            onChannelClick = onChannelClick,
            onSettingsClick = onSettingsClick,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        )
        MainArea(
            currentChannel = currentChannel,
            playerState = playerState,
            modifier = Modifier
                .fillMaxHeight()
                .weight(2.5f)
        )
    }
}

@Composable
private fun ChannelList(
    channels: List<Channel>,
    currentChannel: Channel?,
    onChannelClick: (Channel) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(start = 24.dp, top = 24.dp, bottom = 24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Canais",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = onSettingsClick) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Configurações",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(end = 24.dp),
            color = MaterialTheme.colorScheme.outline
        )

        if (channels.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhum canal encontrado.\nAbra as configurações para adicionar uma playlist.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(end = 24.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = 8.dp, end = 24.dp)
            ) {
                items(channels, key = { it.id }) { channel ->
                    ChannelItem(
                        channel = channel,
                        selected = channel.id == currentChannel?.id,
                        onClick = { onChannelClick(channel) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChannelItem(
    channel: Channel,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = channel.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (selected)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    if (channel.category.isNotBlank()) {
                        Text(
                            text = channel.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = (if (selected)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant).copy(alpha = 0.7f)
                        )
                    }

                    if (channel.isLive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = MaterialTheme.shapes.extraSmall,
                            color = MaterialTheme.colorScheme.error
                        ) {
                            Text(
                                text = "LIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onError,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MainArea(
    currentChannel: Channel?,
    playerState: MainViewModel.PlayerState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.padding(top = 24.dp, end = 24.dp, bottom = 24.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface
    ) {
        if (currentChannel != null) {
            VideoPlayer(
                modifier = Modifier.fillMaxSize(),
                channel = currentChannel,
                isPlaying = playerState.isPlaying,
                isLoading = playerState.isLoading,
                volume = playerState.volume,
                onPlayerReady = {
                    // Callback quando o player está pronto
                },
                onPlayerError = { error ->
                    // Log do erro
                    android.util.Log.e("MainScreen", "Erro do player: $error")
                },
                onPlaybackStateChanged = { isPlaying ->
                    // Atualizar estado de reprodução se necessário
                }
            )
        } else {
            WelcomeScreen()
        }
    }
}

@Composable
private fun WelcomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "Bem-vindo ao Xcloud TV 2025",
                style = MaterialTheme.typography.displaySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Selecione um canal na lista para começar a assistir.",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ErrorSnackbar(error: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Snackbar(
            action = {
                TextButton(onClick = onDismiss) {
                    Text("Fechar")
                }
            },
        ) {
            Text(text = error)
        }
    }
}

@Composable
private fun SuccessSnackbar(message: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Snackbar(
            action = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            },
        ) {
            Text(text = message)
        }
    }
}