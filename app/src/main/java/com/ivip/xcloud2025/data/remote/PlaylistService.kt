package com.ivip.xcloudtv2025.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Serviço responsável por baixar playlists M3U de URLs remotas
 * Suporta autenticação básica e diferentes formatos de URL
 */
class PlaylistService {

    companion object {
        private const val DEFAULT_TIMEOUT = 30L
        private const val DEFAULT_USER_AGENT = "Xcloud TV 2025/1.0"

        // Headers comuns para requests IPTV
        private const val HEADER_USER_AGENT = "User-Agent"
        private const val HEADER_ACCEPT = "Accept"
        private const val HEADER_ACCEPT_LANGUAGE = "Accept-Language"
        private const val HEADER_CACHE_CONTROL = "Cache-Control"

        // Valores padrão para headers
        private const val ACCEPT_VALUE = "application/x-mpegURL,application/vnd.apple.mpegurl,application/json,text/plain,*/*"
        private const val ACCEPT_LANGUAGE_VALUE = "pt-BR,pt;q=0.9,en;q=0.8"
        private const val CACHE_CONTROL_VALUE = "no-cache"
    }

    private val httpClient: OkHttpClient by lazy {
        createHttpClient()
    }

    /**
     * Baixa uma playlist M3U de uma URL
     * @param url URL da playlist M3U
     * @param username Nome de usuário para autenticação (opcional)
     * @param password Senha para autenticação (opcional)
     * @return Resultado contendo o conteúdo da playlist ou erro
     */
    suspend fun fetchPlaylist(
        url: String,
        username: String = "",
        password: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (url.isBlank()) {
                return@withContext Result.failure(
                    IllegalArgumentException("URL da playlist não pode estar vazia")
                )
            }

            val processedUrl = processUrl(url, username, password)
            val request = buildRequest(processedUrl, username, password)

            val response = httpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("Erro HTTP ${response.code}: ${response.message}")
                )
            }

            val contentType = response.header("Content-Type") ?: ""
            if (!isValidContentType(contentType)) {
                // Log do tipo de conteúdo mas não falha, pois alguns servidores não definem corretamente
                println("Aviso: Tipo de conteúdo inesperado: $contentType")
            }

            val content = response.body?.string() ?: ""

            if (content.isBlank()) {
                return@withContext Result.failure(
                    IOException("Playlist vazia ou não encontrada")
                )
            }

            // Valida se o conteúdo parece ser M3U
            if (!content.trim().startsWith("#EXTM3U") && !content.contains("#EXTINF")) {
                return@withContext Result.failure(
                    IllegalArgumentException("Conteúdo não parece ser uma playlist M3U válida")
                )
            }

            Result.success(content)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Testa se uma URL de playlist é válida e acessível
     * @param url URL para testar
     * @param username Nome de usuário para autenticação (opcional)
     * @param password Senha para autenticação (opcional)
     * @return Resultado do teste com informações da playlist
     */
    suspend fun testPlaylistUrl(
        url: String,
        username: String = "",
        password: String = ""
    ): Result<PlaylistTestResult> = withContext(Dispatchers.IO) {
        try {
            if (url.isBlank()) {
                return@withContext Result.failure(
                    IllegalArgumentException("URL não pode estar vazia")
                )
            }

            val processedUrl = processUrl(url, username, password)
            val request = buildRequest(processedUrl, username, password)

            val startTime = System.currentTimeMillis()
            val response = httpClient.newCall(request).execute()
            val responseTime = System.currentTimeMillis() - startTime

            val testResult = PlaylistTestResult(
                success = response.isSuccessful,
                httpCode = response.code,
                httpMessage = response.message,
                responseTime = responseTime,
                contentType = response.header("Content-Type") ?: "unknown",
                contentLength = response.body?.contentLength() ?: -1,
                serverInfo = response.header("Server") ?: "unknown"
            )

            if (response.isSuccessful) {
                val content = response.body?.string() ?: ""
                testResult.hasValidContent = content.trim().startsWith("#EXTM3U") || content.contains("#EXTINF")
                testResult.contentPreview = content.take(200)

                // Conta linhas EXTINF para estimar número de canais
                testResult.estimatedChannels = content.count { line -> line.toString().contains("#EXTINF") }
            }

            Result.success(testResult)

        } catch (e: Exception) {
            val testResult = PlaylistTestResult(
                success = false,
                error = e.message ?: "Erro desconhecido"
            )
            Result.success(testResult) // Retorna sucesso com erro interno para não quebrar o fluxo
        }
    }

    /**
     * Baixa apenas os headers de uma playlist para verificação rápida
     */
    suspend fun checkPlaylistHeaders(
        url: String,
        username: String = "",
        password: String = ""
    ): Result<Map<String, String>> = withContext(Dispatchers.IO) {
        try {
            val processedUrl = processUrl(url, username, password)
            val request = buildRequest(processedUrl, username, password, headOnly = true)

            val response = httpClient.newCall(request).execute()

            val headers = mutableMapOf<String, String>()
            response.headers.forEach { header ->
                headers[header.first] = header.second
            }

            headers["status_code"] = response.code.toString()
            headers["status_message"] = response.message

            Result.success(headers.toMap())

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Processa a URL para diferentes formatos de IPTV
     */
    private fun processUrl(url: String, username: String, password: String): String {
        var processedUrl = url.trim()

        // Remove espaços e caracteres inválidos
        processedUrl = processedUrl.replace(" ", "%20")

        // Se não tem protocolo, adiciona http://
        if (!processedUrl.startsWith("http://") && !processedUrl.startsWith("https://")) {
            processedUrl = "http://$processedUrl"
        }

        // Para URLs que incluem credenciais no formato especial do IPTV
        if (username.isNotBlank() && password.isNotBlank() &&
            !processedUrl.contains("username=") && !processedUrl.contains("password=")) {

            val separator = if (processedUrl.contains("?")) "&" else "?"
            processedUrl += "${separator}username=$username&password=$password"
        }

        return processedUrl
    }

    /**
     * Constrói o request HTTP com headers apropriados
     */
    private fun buildRequest(
        url: String,
        username: String,
        password: String,
        headOnly: Boolean = false
    ): Request {
        val builder = Request.Builder()
            .url(url)
            .header(HEADER_USER_AGENT, DEFAULT_USER_AGENT)
            .header(HEADER_ACCEPT, ACCEPT_VALUE)
            .header(HEADER_ACCEPT_LANGUAGE, ACCEPT_LANGUAGE_VALUE)
            .header(HEADER_CACHE_CONTROL, CACHE_CONTROL_VALUE)

        if (headOnly) {
            builder.head()
        }

        // Adiciona autenticação básica se necessário
        if (username.isNotBlank() && password.isNotBlank() &&
            !url.contains("username=") && !url.contains("password=")) {
            val credentials = Credentials.basic(username, password)
            builder.header("Authorization", credentials)
        }

        return builder.build()
    }

    /**
     * Verifica se o tipo de conteúdo é válido para playlists M3U
     */
    private fun isValidContentType(contentType: String): Boolean {
        val validTypes = listOf(
            "application/x-mpegurl",
            "application/vnd.apple.mpegurl",
            "audio/x-mpegurl",
            "application/m3u8",
            "text/plain",
            "application/octet-stream"
        )

        return validTypes.any { contentType.contains(it, ignoreCase = true) }
    }

    /**
     * Cria o cliente HTTP com configurações otimizadas
     */
    private fun createHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT * 2, TimeUnit.SECONDS)
            .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                // Interceptor para retry customizado
                var request = chain.request()
                var response = chain.proceed(request)

                var retryCount = 0
                while (!response.isSuccessful && retryCount < 2) {
                    retryCount++
                    response.close()
                    response = chain.proceed(request)
                }

                response
            }
            .build()
    }

    /**
     * Classe para resultado do teste de playlist
     */
    data class PlaylistTestResult(
        val success: Boolean = false,
        val httpCode: Int = 0,
        val httpMessage: String = "",
        val responseTime: Long = 0,
        val contentType: String = "",
        val contentLength: Long = -1,
        val serverInfo: String = "",
        val error: String? = null,
        var hasValidContent: Boolean = false,
        var contentPreview: String = "",
        var estimatedChannels: Int = 0
    ) {
        val isAccessible: Boolean
            get() = success && httpCode == 200

        val isPlaylistValid: Boolean
            get() = isAccessible && hasValidContent

        fun getSummary(): String {
            return when {
                !success -> error ?: "Erro de conexão"
                !isAccessible -> "HTTP $httpCode - $httpMessage"
                !hasValidContent -> "URL acessível mas não é uma playlist M3U válida"
                else -> "Playlist válida com aproximadamente $estimatedChannels canais"
            }
        }
    }

    /**
     * Enum para tipos de erro comuns
     */
    enum class PlaylistError(val message: String) {
        NETWORK_ERROR("Erro de conexão de rede"),
        INVALID_URL("URL inválida"),
        AUTH_REQUIRED("Autenticação necessária"),
        NOT_FOUND("Playlist não encontrada"),
        INVALID_FORMAT("Formato de playlist inválido"),
        TIMEOUT("Timeout na conexão"),
        UNKNOWN("Erro desconhecido")
    }
}