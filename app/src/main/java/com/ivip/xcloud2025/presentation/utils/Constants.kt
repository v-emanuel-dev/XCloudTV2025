package com.ivip.xcloudtv2025.presentation.utils

/**
 * Constantes utilizadas em toda a aplicação
 */
object Constants {

    // Informações da aplicação
    const val APP_NAME = "Xcloud TV 2025"
    const val APP_VERSION = "1.0.0"
    const val APP_PACKAGE = "com.ivip.xcloudtv2025"

    // URLs e endpoints
    const val DEFAULT_TIMEOUT = 30000L
    const val DEFAULT_RETRY_COUNT = 3
    const val MAX_CHANNELS_PER_PLAYLIST = 10000

    // Configurações do player
    const val DEFAULT_BUFFER_DURATION = 15000 // 15 segundos
    const val MIN_BUFFER_DURATION = 5000     // 5 segundos
    const val MAX_BUFFER_DURATION = 60000    // 60 segundos
    const val DEFAULT_VOLUME = 1.0f

    // Configurações de interface
    const val DEFAULT_GRID_COLUMNS = 4
    const val MIN_GRID_COLUMNS = 2
    const val MAX_GRID_COLUMNS = 8
    const val CARD_ASPECT_RATIO = 16f / 9f
    const val CARD_CORNER_RADIUS = 8

    // Animações (duração em milissegundos)
    const val ANIMATION_DURATION_SHORT = 150
    const val ANIMATION_DURATION_MEDIUM = 300
    const val ANIMATION_DURATION_LONG = 500

    // Dimensões para Android TV
    const val TV_SAFE_AREA_PADDING = 48 // dp
    const val TV_FOCUS_BORDER_WIDTH = 4 // dp
    const val TV_CARD_ELEVATION = 8 // dp
    const val TV_CARD_FOCUSED_ELEVATION = 16 // dp

    // Configurações de navegação
    const val NAVIGATION_ANIMATION_DURATION = 300

    // Formatos suportados
    val SUPPORTED_PLAYLIST_FORMATS = listOf("m3u", "m3u8", "txt")
    val SUPPORTED_VIDEO_FORMATS = listOf("mp4", "mkv", "avi", "ts", "m3u8", "hls")

    // Categorias padrão
    val DEFAULT_CATEGORIES = listOf(
        "Todos",
        "Favoritos",
        "Recentes",
        "Esportes",
        "Notícias",
        "Filmes",
        "Séries",
        "Documentários",
        "Música",
        "Infantil",
        "Religioso",
        "Internacional"
    )

    // Códigos de erro
    object ErrorCodes {
        const val NETWORK_ERROR = 1001
        const val PLAYLIST_INVALID = 1002
        const val PLAYER_ERROR = 1003
        const val DATABASE_ERROR = 1004
        const val AUTHENTICATION_ERROR = 1005
        const val PERMISSION_ERROR = 1006
    }

    // Preferências (chaves)
    object PreferenceKeys {
        const val PLAYLIST_URL = "playlist_url"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val LAST_CHANNEL_ID = "last_channel_id"
        const val GRID_COLUMNS = "grid_columns"
        const val THEME_MODE = "theme_mode"
        const val AUTO_PLAY = "auto_play"
        const val SHOW_CHANNEL_NUMBERS = "show_channel_numbers"
    }

    // Mensagens para usuário
    object Messages {
        const val LOADING = "Carregando..."
        const val NO_CHANNELS = "Nenhum canal encontrado"
        const val NO_INTERNET = "Sem conexão com a internet"
        const val PLAYLIST_UPDATED = "Playlist atualizada com sucesso"
        const val PLAYLIST_ERROR = "Erro ao carregar playlist"
        const val PLAYER_ERROR = "Erro ao reproduzir canal"
        const val SETTINGS_SAVED = "Configurações salvas"
    }

    // Configurações de qualidade de vídeo
    object VideoQuality {
        const val AUTO = "AUTO"
        const val LOW = "LOW"
        const val MEDIUM = "MEDIUM"
        const val HIGH = "HIGH"
        const val ULTRA = "ULTRA"

        val QUALITY_OPTIONS = listOf(AUTO, LOW, MEDIUM, HIGH, ULTRA)
        val QUALITY_LABELS = mapOf(
            AUTO to "Automático",
            LOW to "Baixa (480p)",
            MEDIUM to "Média (720p)",
            HIGH to "Alta (1080p)",
            ULTRA to "Ultra (4K)"
        )
    }

    // Configurações de tema
    object ThemeMode {
        const val LIGHT = "LIGHT"
        const val DARK = "DARK"
        const val SYSTEM = "SYSTEM"

        val THEME_OPTIONS = listOf(LIGHT, DARK, SYSTEM)
        val THEME_LABELS = mapOf(
            LIGHT to "Claro",
            DARK to "Escuro",
            SYSTEM to "Sistema"
        )
    }

    // Logs e debugging
    object LogTags {
        const val MAIN = "XcloudMain"
        const val PLAYER = "XcloudPlayer"
        const val PLAYLIST = "XcloudPlaylist"
        const val DATABASE = "XcloudDB"
        const val NETWORK = "XcloudNetwork"
        const val SETTINGS = "XcloudSettings"
    }

    // Regex patterns
    object Patterns {
        const val URL_PATTERN = "^(http|https)://.*"
        const val M3U_HEADER = "#EXTM3U"
        const val M3U_INFO = "#EXTINF:"
        const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    }

    // Notificações
    object Notifications {
        const val CHANNEL_ID = "xcloud_playback"
        const val CHANNEL_NAME = "Reprodução"
        const val CHANNEL_DESCRIPTION = "Notificações de reprodução de mídia"
        const val NOTIFICATION_ID = 1001
    }

    // Intents
    object Intents {
        const val ACTION_PLAY_CHANNEL = "com.ivip.xcloudtv2025.PLAY_CHANNEL"
        const val ACTION_PAUSE_PLAYBACK = "com.ivip.xcloudtv2025.PAUSE_PLAYBACK"
        const val ACTION_STOP_PLAYBACK = "com.ivip.xcloudtv2025.STOP_PLAYBACK"
        const val EXTRA_CHANNEL_ID = "channel_id"
        const val EXTRA_CHANNEL_URL = "channel_url"
    }

    // Configurações de cache
    object Cache {
        const val MAX_CACHE_SIZE = 50 * 1024 * 1024L // 50MB
        const val CACHE_EXPIRY_DAYS = 7
        const val IMAGE_CACHE_SIZE = 20 * 1024 * 1024L // 20MB
    }

    // Configurações de rede
    object Network {
        const val CONNECT_TIMEOUT = 30L // segundos
        const val READ_TIMEOUT = 60L // segundos
        const val WRITE_TIMEOUT = 30L // segundos
        const val MAX_RETRIES = 3
        const val RETRY_DELAY = 1000L // milissegundos
    }
}