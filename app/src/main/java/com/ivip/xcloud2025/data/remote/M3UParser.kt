package com.ivip.xcloudtv2025.data.remote

import com.ivip.xcloudtv2025.domain.model.Channel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.StringReader

/**
 * Parser para arquivos M3U/M3U8 de playlists IPTV
 * Suporta formato M3U estendido com metadados
 */
class M3UParser {

    companion object {
        private const val M3U_HEADER = "#EXTM3U"
        private const val M3U_INFO = "#EXTINF:"
        private const val M3U_GROUP = "#EXTGRP:"
        private const val M3U_LOGO = "#EXTIMG:"

        // Regex patterns para extrair informações
        private val TVG_ID_PATTERN = """tvg-id="([^"]*)"""""".toRegex()
        private val TVG_NAME_PATTERN = """tvg-name="([^"]*)"""""".toRegex()
        private val TVG_LOGO_PATTERN = """tvg-logo="([^"]*)"""""".toRegex()
        private val GROUP_TITLE_PATTERN = """group-title="([^"]*)"""""".toRegex()
        private val LANGUAGE_PATTERN = """tvg-language="([^"]*)"""""".toRegex()
        private val COUNTRY_PATTERN = """tvg-country="([^"]*)"""""".toRegex()
        private val RADIO_PATTERN = """radio="([^"]*)"""""".toRegex()

        // Pattern para extrair duração e nome do canal
        private val EXTINF_PATTERN = """#EXTINF:(-?\d+(?:\.\d+)?)\s*,(.*)""".toRegex()
    }

    /**
     * Faz o parse de uma string M3U e retorna uma lista de canais
     * @param m3uContent Conteúdo do arquivo M3U como string
     * @return Lista de canais parseados
     */
    suspend fun parseM3U(m3uContent: String): Result<List<Channel>> = withContext(Dispatchers.Default) {
        try {
            val channels = mutableListOf<Channel>()
            val reader = BufferedReader(StringReader(m3uContent))

            var currentChannel: ChannelBuilder? = null
            var channelId = 1L
            var lineNumber = 0

            reader.useLines { lines ->
                val linesList = lines.toList()

                // Verifica se é um arquivo M3U válido
                if (linesList.isEmpty() || !linesList.first().trim().startsWith(M3U_HEADER)) {
                    return@withContext Result.failure(
                        IllegalArgumentException("Arquivo M3U inválido - cabeçalho #EXTM3U não encontrado")
                    )
                }

                for (line in linesList) {
                    lineNumber++
                    val trimmedLine = line.trim()

                    when {
                        trimmedLine.startsWith(M3U_INFO) -> {
                            // Nova entrada de canal
                            currentChannel = parseExtInf(trimmedLine, channelId++)
                        }

                        trimmedLine.startsWith(M3U_GROUP) -> {
                            // Grupo do canal (formato alternativo)
                            currentChannel?.groupTitle = trimmedLine.substringAfter(M3U_GROUP)
                        }

                        trimmedLine.startsWith(M3U_LOGO) -> {
                            // Logo do canal (formato alternativo)
                            currentChannel?.logoUrl = trimmedLine.substringAfter(M3U_LOGO)
                        }

                        trimmedLine.startsWith("http") -> {
                            // URL do stream
                            currentChannel?.let { builder ->
                                builder.url = trimmedLine
                                val channel = builder.build()
                                if (channel.isValid) {
                                    channels.add(channel)
                                } else {
                                    println("Canal inválido na linha $lineNumber: ${channel.name}")
                                }
                            }
                            currentChannel = null
                        }

                        trimmedLine.startsWith("#") -> {
                            // Outros comentários M3U - ignora por enquanto
                            continue
                        }

                        trimmedLine.isNotEmpty() -> {
                            // Pode ser uma URL sem http ou formato não padrão
                            if (currentChannel != null && trimmedLine.contains(".")) {
                                currentChannel?.let { builder ->
                                    builder.url = trimmedLine
                                    val channel = builder.build()
                                    if (channel.isValid) {
                                        channels.add(channel)
                                    }
                                }
                                currentChannel = null
                            }
                        }
                    }
                }
            }

            reader.close()

            if (channels.isEmpty()) {
                return@withContext Result.failure(
                    IllegalArgumentException("Nenhum canal válido encontrado na playlist")
                )
            }

            Result.success(channels.distinctBy { it.url }) // Remove duplicatas por URL

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Faz o parse da linha #EXTINF
     */
    private fun parseExtInf(extinfLine: String, id: Long): ChannelBuilder {
        val builder = ChannelBuilder(id)

        // Extrai duração e nome básico
        val match = EXTINF_PATTERN.find(extinfLine)
        if (match != null) {
            val duration = match.groupValues[1].toDoubleOrNull() ?: -1.0
            val nameAndAttributes = match.groupValues[2]

            // Separa atributos do nome do canal
            val parts = nameAndAttributes.split(",")
            val attributePart = if (parts.size > 1) parts[0] else ""
            val namePart = if (parts.size > 1) parts.drop(1).joinToString(",") else nameAndAttributes

            builder.name = namePart.trim()
            builder.isLive = duration == -1.0 // -1 indica live stream

            // Extrai atributos TVG
            extractTvgAttributes(attributePart, builder)
        }

        return builder
    }

    /**
     * Extrai atributos TVG da linha EXTINF
     */
    private fun extractTvgAttributes(attributeLine: String, builder: ChannelBuilder) {
        // tvg-id
        TVG_ID_PATTERN.find(attributeLine)?.groupValues?.get(1)?.let { tvgId ->
            if (tvgId.isNotBlank()) builder.tvgId = tvgId
        }

        // tvg-name
        TVG_NAME_PATTERN.find(attributeLine)?.groupValues?.get(1)?.let { tvgName ->
            if (tvgName.isNotBlank()) builder.tvgName = tvgName
        }

        // tvg-logo
        TVG_LOGO_PATTERN.find(attributeLine)?.groupValues?.get(1)?.let { tvgLogo ->
            if (tvgLogo.isNotBlank()) builder.logoUrl = tvgLogo
        }

        // group-title (categoria)
        GROUP_TITLE_PATTERN.find(attributeLine)?.groupValues?.get(1)?.let { groupTitle ->
            if (groupTitle.isNotBlank()) {
                builder.groupTitle = groupTitle
                builder.category = groupTitle // Usa group-title como categoria padrão
            }
        }

        // tvg-language
        LANGUAGE_PATTERN.find(attributeLine)?.groupValues?.get(1)?.let { language ->
            if (language.isNotBlank()) builder.language = language
        }

        // tvg-country
        COUNTRY_PATTERN.find(attributeLine)?.groupValues?.get(1)?.let { country ->
            if (country.isNotBlank()) builder.country = country
        }

        // radio (para identificar rádios)
        RADIO_PATTERN.find(attributeLine)?.groupValues?.get(1)?.let { radio ->
            if (radio.equals("true", ignoreCase = true)) {
                builder.category = "Rádio"
                builder.isLive = true
            }
        }
    }

    /**
     * Valida se o conteúdo é um M3U válido
     */
    fun isValidM3U(content: String): Boolean {
        return content.trim().startsWith(M3U_HEADER) &&
                content.contains(M3U_INFO) &&
                content.contains("http")
    }

    /**
     * Extrai estatísticas da playlist
     */
    suspend fun getPlaylistStats(m3uContent: String): PlaylistStats = withContext(Dispatchers.Default) {
        try {
            val result = parseM3U(m3uContent)
            if (result.isSuccess) {
                val channels = result.getOrNull() ?: emptyList()
                val categories = channels.groupBy { it.category }
                val countries = channels.mapNotNull { it.country }.distinct()
                val languages = channels.mapNotNull { it.language }.distinct()

                PlaylistStats(
                    totalChannels = channels.size,
                    categories = categories.mapValues { it.value.size },
                    countries = countries,
                    languages = languages,
                    liveChannels = channels.count { it.isLive },
                    vodChannels = channels.count { !it.isLive }
                )
            } else {
                PlaylistStats()
            }
        } catch (e: Exception) {
            PlaylistStats()
        }
    }

    /**
     * Builder pattern para construir canais
     */
    private data class ChannelBuilder(
        val id: Long,
        var name: String = "",
        var description: String = "",
        var url: String = "",
        var category: String = "Geral",
        var logoUrl: String? = null,
        var isLive: Boolean = true,
        var groupTitle: String? = null,
        var tvgId: String? = null,
        var tvgName: String? = null,
        var language: String? = null,
        var country: String? = null
    ) {
        fun build(): Channel {
            return Channel(
                id = id,
                name = if (name.isBlank()) "Canal $id" else name,
                description = description,
                url = url,
                category = category,
                logoUrl = logoUrl,
                isLive = isLive,
                groupTitle = groupTitle,
                tvgId = tvgId,
                tvgLogo = logoUrl, // tvg-logo é armazenado em logoUrl
                tvgName = tvgName,
                language = language,
                country = country
            )
        }
    }

    /**
     * Classe para estatísticas da playlist
     */
    data class PlaylistStats(
        val totalChannels: Int = 0,
        val categories: Map<String, Int> = emptyMap(),
        val countries: List<String> = emptyList(),
        val languages: List<String> = emptyList(),
        val liveChannels: Int = 0,
        val vodChannels: Int = 0
    ) {
        val hasValidData: Boolean
            get() = totalChannels > 0

        fun getSummary(): String {
            return buildString {
                append("$totalChannels canais")
                if (categories.isNotEmpty()) {
                    append(" em ${categories.size} categorias")
                }
                if (countries.isNotEmpty()) {
                    append(", ${countries.size} países")
                }
                if (liveChannels > 0 && vodChannels > 0) {
                    append(" ($liveChannels ao vivo, $vodChannels VOD)")
                } else if (liveChannels > 0) {
                    append(" (todos ao vivo)")
                } else if (vodChannels > 0) {
                    append(" (todos VOD)")
                }
            }
        }
    }
}