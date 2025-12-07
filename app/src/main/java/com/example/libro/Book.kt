package com.example.libro

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Book(
    val title: String,
    val author: String,
    val year: Int,
    val tags: List<String>,
    val status: String,
    val rating: Int,
    val commentCount: Int,
    val bookmarkCount: Int,
    val coverUrl: String? = null,
    val publisher: String? = null,
    val description: String? = null,
    val shelfPosition: String? = "Не указано",
    val isbn: String? = null,
    val shelfName: String? = null,
    val shelfLocation: String? = null,
    val shelfNumber: String? = null,
    val placeNumber: String? = null
) : Parcelable