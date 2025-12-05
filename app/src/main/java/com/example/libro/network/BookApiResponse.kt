package com.example.libro.network

import kotlinx.serialization.Serializable

@Serializable
data class BookApiResponse(val items: List<BookItem>? = null)

@Serializable
data class BookItem(val volumeInfo: VolumeInfo? = null)

@Serializable
data class VolumeInfo(
    val title: String? = null,
    val authors: List<String>? = null,
    val publishedDate: String? = null,
    val description: String? = null,
    val imageLinks: ImageLinks? = null
)

@Serializable
data class ImageLinks(val thumbnail: String? = null)