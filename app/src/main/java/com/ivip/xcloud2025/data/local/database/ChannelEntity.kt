package com.ivip.xcloudtv2025.data.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.ivip.xcloudtv2025.domain.model.Channel

/**
 * Entidade do Room para armazenar canais no banco de dados local
 */
@Entity(
    tableName = "channels",
    indices = [
        Index(value = ["name"], unique = false),
        Index(value = ["category"], unique = false),
        Index(value = ["is_favorite"], unique = false),
        Index(value = ["last_watched"], unique = false)
    ]
)
data class ChannelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "category")
    val category: String = "Geral",

    @ColumnInfo(name = "logo_url")
    val logoUrl: String? = null,

    @ColumnInfo(name = "is_live")
    val isLive: Boolean = true,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "group_title")
    val groupTitle: String? = null,

    @ColumnInfo(name = "tvg_id")
    val tvgId: String? = null,

    @ColumnInfo(name = "tvg_logo")
    val tvgLogo: String? = null,

    @ColumnInfo(name = "tvg_name")
    val tvgName: String? = null,

    @ColumnInfo(name = "language")
    val language: String? = null,

    @ColumnInfo(name = "country")
    val country: String? = null,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,

    @ColumnInfo(name = "last_watched")
    val lastWatched: Long = 0L,

    @ColumnInfo(name = "added_date")
    val addedDate: Long = System.currentTimeMillis()
)

/**
 * Extensões para converter entre ChannelEntity e Channel
 */
fun ChannelEntity.toDomain(): Channel {
    return Channel(
        id = id,
        name = name,
        description = description,
        url = url,
        category = category,
        logoUrl = logoUrl,
        isLive = isLive,
        isFavorite = isFavorite,
        groupTitle = groupTitle,
        tvgId = tvgId,
        tvgLogo = tvgLogo,
        tvgName = tvgName,
        language = language,
        country = country,
        isActive = isActive,
        sortOrder = sortOrder,
        lastWatched = lastWatched,
        addedDate = addedDate
    )
}

fun Channel.toEntity(): ChannelEntity {
    return ChannelEntity(
        id = id,
        name = name,
        description = description,
        url = url,
        category = category,
        logoUrl = logoUrl,
        isLive = isLive,
        isFavorite = isFavorite,
        groupTitle = groupTitle,
        tvgId = tvgId,
        tvgLogo = tvgLogo,
        tvgName = tvgName,
        language = language,
        country = country,
        isActive = isActive,
        sortOrder = sortOrder,
        lastWatched = lastWatched,
        addedDate = addedDate
    )
}