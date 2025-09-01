// 📁 Arquivo: Channel.kt - VERSÃO CORRIGIDA
// 📍 Localização: app/src/main/java/com/ivip/xcloudtv2025/domain/model/Channel.kt

package com.ivip.xcloudtv2025.domain.model

import android.os.Parcelable
import android.os.Parcel

/**
 * Modelo de dados para um canal de TV
 * Representa um canal IPTV com todas as informações necessárias para reprodução
 */
data class Channel(
    val id: Long = 0L,
    val name: String,
    val description: String = "",
    val url: String,
    val category: String = "Geral",
    val logoUrl: String? = null,
    val isLive: Boolean = true,
    val isFavorite: Boolean = false,
    val groupTitle: String? = null,
    val tvgId: String? = null,
    val tvgLogo: String? = null,
    val tvgName: String? = null,
    val language: String? = null,
    val country: String? = null,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
    val lastWatched: Long = 0L, // timestamp da última vez que foi assistido
    val addedDate: Long = System.currentTimeMillis()
) : Parcelable {

    /**
     * Retorna o nome de exibição do canal
     * Prioriza tvgName se disponível, senão usa name
     */
    val displayName: String
        get() = tvgName?.takeIf { it.isNotBlank() } ?: name

    /**
     * Retorna a URL do logo do canal
     * Prioriza tvgLogo se disponível, senão usa logoUrl
     */
    val displayLogo: String?
        get() = tvgLogo?.takeIf { it.isNotBlank() } ?: logoUrl

    /**
     * Verifica se o canal é válido para reprodução
     */
    val isValid: Boolean
        get() = name.isNotBlank() && url.isNotBlank() && isActive

    /**
     * Retorna uma versão simplificada do canal para logs
     */
    override fun toString(): String {
        return "Channel(id=$id, name='$name', category='$category', isLive=$isLive, sortOrder=$sortOrder)"
    }

    // Manual Parcelable implementation
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "Geral",
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(url)
        parcel.writeString(category)
        parcel.writeString(logoUrl)
        parcel.writeByte(if (isLive) 1 else 0)
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeString(groupTitle)
        parcel.writeString(tvgId)
        parcel.writeString(tvgLogo)
        parcel.writeString(tvgName)
        parcel.writeString(language)
        parcel.writeString(country)
        parcel.writeByte(if (isActive) 1 else 0)
        parcel.writeInt(sortOrder)
        parcel.writeLong(lastWatched)
        parcel.writeLong(addedDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Channel> {
        override fun createFromParcel(parcel: Parcel): Channel {
            return Channel(parcel)
        }

        override fun newArray(size: Int): Array<Channel?> {
            return arrayOfNulls(size)
        }
    }
}

/**
 * Extensão para criar uma cópia do canal marcando como favorito
 */
fun Channel.toggleFavorite(): Channel {
    return this.copy(isFavorite = !isFavorite)
}

/**
 * Extensão para atualizar o timestamp da última visualização
 */
fun Channel.markAsWatched(): Channel {
    return this.copy(lastWatched = System.currentTimeMillis())
}

/**
 * Lista de canais padrão gratuitos que vem com o app
 * CORRIGIDO: Definindo sortOrder explicitamente para garantir a ordem
 */
object DefaultChannels {
    val DEFAULT_FREE_CHANNELS = listOf(
        Channel(
            id = 1,
            name = "Red Bull TV",
            description = "Esportes radicais e eventos",
            url = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8",
            category = "Esportes",
            logoUrl = "https://i.imgur.com/BcXewtB.png",
            isLive = true,
            country = "Austria",
            language = "English",
            sortOrder = 1 // EXPLICITAMENTE DEFININDO A ORDEM
        ),
        Channel(
            id = 2,
            name = "RT News",
            description = "Russia Today - Notícias 24/7",
            url = "https://rt-glb.rttv.com/live/rtnews/playlist.m3u8",
            category = "Notícias",
            logoUrl = "https://i.imgur.com/QX5hWvW.png",
            isLive = true,
            country = "Russia",
            language = "English",
            sortOrder = 2 // EXPLICITAMENTE DEFININDO A ORDEM
        ),
        Channel(
            id = 3,
            name = "Al Jazeera English",
            description = "Canal de notícias internacional",
            url = "https://live-hls-web-aje.getaj.net/AJE/index.m3u8",
            category = "Notícias",
            logoUrl = "https://i.imgur.com/7V012zQ.png",
            isLive = true,
            country = "Qatar",
            language = "English",
            sortOrder = 3 // EXPLICITAMENTE DEFININDO A ORDEM
        ),
        Channel(
            id = 4,
            name = "Fashion TV",
            description = "Moda e estilo de vida",
            url = "https://fashiontv-fashiontv-1-eu.rakuten.wurl.tv/playlist.m3u8",
            category = "Estilo",
            logoUrl = "https://i.imgur.com/fYQkS6L.png",
            isLive = true,
            country = "France",
            language = "English",
            sortOrder = 4 // EXPLICITAMENTE DEFININDO A ORDEM
        ),
        Channel(
            id = 5,
            name = "Bloomberg TV",
            description = "Notícias financeiras",
            url = "https://bloomberg.com/media-manifest/streams/phoenix-us.m3u8",
            category = "Economia",
            logoUrl = "https://i.imgur.com/OuogLHX.png",
            isLive = true,
            country = "USA",
            language = "English",
            sortOrder = 5 // EXPLICITAMENTE DEFININDO A ORDEM - SERÁ O ÚLTIMO
        )
    )

    /**
     * Retorna os canais padrão organizados por categoria
     */
    fun getChannelsByCategory(): Map<String, List<Channel>> {
        return DEFAULT_FREE_CHANNELS.groupBy { it.category }
    }

    /**
     * Retorna categorias disponíveis nos canais padrão
     */
    fun getAvailableCategories(): List<String> {
        return DEFAULT_FREE_CHANNELS.map { it.category }.distinct().sorted()
    }
}