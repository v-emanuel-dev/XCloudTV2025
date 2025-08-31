package com.ivip.xcloudtv2025.presentation.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ivip.xcloudtv2025.presentation.screens.main.MainViewModel
import com.ivip.xcloudtv2025.presentation.theme.XcloudTextStyles

/**
 * Dialog de configurações para playlist IPTV
 * Interface otimizada para Android TV com navegação D-pad
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    currentSettings: MainViewModel.PlaylistSettings,
    onUpdatePlaylist: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var playlistUrl by remember { mutableStateOf(currentSettings.url) }
    var username by remember { mutableStateOf(currentSettings.username) }
    var password by remember { mutableStateOf(currentSettings.password) }
    var useAuthentication by remember { mutableStateOf(currentSettings.useAuth) }
    var showPassword by remember { mutableStateOf(false) }

    // Estados de validação e feedback
    var isValidating by remember { mutableStateOf(false) }
    var validationResult by remember { mutableStateOf<ValidationResult?>(null) }
    var showAdvancedSettings by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 16.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp)
            ) {
                // Header
                SettingsHeader(onDismiss = onDismiss)

                Spacer(modifier = Modifier.height(24.dp))

                // Configurações da Playlist
                PlaylistSection(
                    playlistUrl = playlistUrl,
                    onPlaylistUrlChange = {
                        playlistUrl = it
                        validationResult = null
                    },
                    onValidateUrl = {
                        if (playlistUrl.isNotBlank()) {
                            isValidating = true
                            // Simular validação
                        }
                    },
                    isValidating = isValidating,
                    validationResult = validationResult
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Seção de Autenticação
                AuthenticationSection(
                    useAuthentication = useAuthentication,
                    username = username,
                    password = password,
                    showPassword = showPassword,
                    onUseAuthChange = { useAuthentication = it },
                    onUsernameChange = { username = it },
                    onPasswordChange = { password = it },
                    onTogglePasswordVisibility = { showPassword = !showPassword }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Configurações Avançadas (expansível)
                AdvancedSettingsSection(
                    expanded = showAdvancedSettings,
                    onExpandChange = { showAdvancedSettings = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botões de ação
                ActionButtons(
                    canSave = playlistUrl.isNotBlank(),
                    onSave = {
                        val finalUsername = if (useAuthentication) username else ""
                        val finalPassword = if (useAuthentication) password else ""
                        onUpdatePlaylist(playlistUrl, finalUsername, finalPassword)
                    },
                    onCancel = onDismiss,
                    onTestConnection = {
                        isValidating = true
                    }
                )

                // Informações adicionais
                Spacer(modifier = Modifier.height(16.dp))
                HelpSection()
            }
        }
    }
}

@Composable
private fun SettingsHeader(
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Configurações",
            style = XcloudTextStyles.CategoryTitle,
            color = MaterialTheme.colorScheme.onSurface
        )

        IconButton(onClick = onDismiss) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Fechar",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistSection(
    playlistUrl: String,
    onPlaylistUrlChange: (String) -> Unit,
    onValidateUrl: () -> Unit,
    isValidating: Boolean,
    validationResult: ValidationResult?
) {
    val focusManager = LocalFocusManager.current

    Column {
        Text(
            text = "Playlist IPTV",
            style = XcloudTextStyles.SettingsTitle,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Cole a URL da sua playlist M3U/M3U8",
            style = XcloudTextStyles.SettingsSubtitle,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = playlistUrl,
            onValueChange = onPlaylistUrlChange,
            label = { Text("URL da Playlist") },
            placeholder = { Text("https://exemplo.com/playlist.m3u8") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Uri,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onValidateUrl()
                }
            ),
            trailingIcon = {
                Row {
                    if (isValidating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else if (playlistUrl.isNotBlank()) {
                        IconButton(onClick = onValidateUrl) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Validar URL",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (playlistUrl.isNotBlank()) {
                        IconButton(
                            onClick = { onPlaylistUrlChange("") }
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Limpar",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            },
            isError = validationResult?.isError == true,
            supportingText = {
                validationResult?.let { result ->
                    Text(
                        text = result.message,
                        color = if (result.isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        if (playlistUrl.isBlank()) {
            ExampleUrls(onUrlSelect = onPlaylistUrlChange)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthenticationSection(
    useAuthentication: Boolean,
    username: String,
    password: String,
    showPassword: Boolean,
    onUseAuthChange: (Boolean) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Autenticação",
                    style = XcloudTextStyles.SettingsTitle,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Habilite se sua playlist requer usuário e senha",
                    style = XcloudTextStyles.SettingsSubtitle,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Switch(
                checked = useAuthentication,
                onCheckedChange = onUseAuthChange
            )
        }

        AnimatedVisibility(visible = useAuthentication) {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Usuário") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Senha") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    trailingIcon = {
                        IconButton(onClick = onTogglePasswordVisibility) {
                            Icon(
                                if (showPassword) Icons.Default.VisibilityOff
                                else Icons.Default.Visibility,
                                contentDescription = if (showPassword) "Ocultar senha" else "Mostrar senha"
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AdvancedSettingsSection(
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit
) {
    Column {
        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onExpandChange(!expanded) },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Configurações Avançadas",
                    style = XcloudTextStyles.SettingsTitle,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Recolher" else "Expandir",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SettingRow(
                        title = "Duração do Buffer",
                        subtitle = "Tempo de buffer em segundos",
                        trailing = {
                            Text(
                                text = "15s",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingRow(
                        title = "Timeout de Conexão",
                        subtitle = "Tempo limite para conectar",
                        trailing = {
                            Text(
                                text = "30s",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    SettingRow(
                        title = "User Agent",
                        subtitle = "Identificação do cliente",
                        trailing = {
                            Text(
                                text = "Padrão",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        trailing()
    }
}

@Composable
private fun ActionButtons(
    canSave: Boolean,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onTestConnection: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Cancel,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Cancelar",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            OutlinedButton(
                onClick = onTestConnection,
                enabled = canSave,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.NetworkCheck,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Testar",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Button(
                onClick = onSave,
                enabled = canSave,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Salvar",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ExampleUrls(onUrlSelect: (String) -> Unit) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = "Exemplos de URLs:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val exampleUrls = listOf(
            "https://exemplo.com/playlist.m3u8" to "Playlist IPTV padrão",
            "http://servidor.com:8080/get.php?username=user&password=pass&type=m3u_plus" to "Xtream Codes API",
            "https://raw.githubusercontent.com/exemplo/playlist.m3u" to "Playlist pública GitHub"
        )

        exampleUrls.forEach { (url, description) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                onClick = { onUrlSelect(url) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = url,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HelpSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 12.dp)
            )

            Column {
                Text(
                    text = "Dicas:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                val tips = listOf(
                    "• Suporte a formatos M3U e M3U8",
                    "• URLs devem começar com http:// ou https://",
                    "• Teste a conexão antes de salvar",
                    "• Canais padrão serão mantidos",
                    "• Use autenticação apenas se necessário"
                )

                tips.forEach { tip ->
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Para mais informações, consulte a documentação do seu provedor IPTV.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val message: String,
    val channelCount: Int = 0,
    val categories: List<String> = emptyList()
) {
    val isError: Boolean = !isValid
}