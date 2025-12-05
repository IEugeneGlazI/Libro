package com.example.libro.ui.library

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Shelf(
    val name: String,
    val location: String,
    val description: String,
    val booksCount: Int,
    val iconName: String = "ic_shelf_books" // Default icon
) : Parcelable