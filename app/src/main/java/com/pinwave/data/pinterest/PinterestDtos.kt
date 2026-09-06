package com.pinwave.data.pinterest

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** DTOs mirroring the documented Pinterest API v5 response shapes. */

@Serializable
data class PagedResponse<T>(
    val items: List<T> = emptyList(),
    val bookmark: String? = null,
)

@Serializable
data class PinterestUserDto(
    val id: String? = null,
    val username: String? = null,
    @SerialName("account_type") val accountType: String? = null,
    @SerialName("profile_image") val profileImage: String? = null,
    @SerialName("website_url") val websiteUrl: String? = null,
)

@Serializable
data class PinDto(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val link: String? = null,
    @SerialName("board_id") val boardId: String? = null,
    val media: PinMediaDto? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class PinMediaDto(
    @SerialName("media_type") val mediaType: String? = null,
    val images: Map<String, PinImageDto>? = null,
)

@Serializable
data class PinImageDto(
    val url: String? = null,
    val width: Int? = null,
    val height: Int? = null,
)

@Serializable
data class BoardDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val privacy: String? = null,
)
