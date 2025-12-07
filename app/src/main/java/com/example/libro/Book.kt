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
    val coverUrl: String? = null, // пока может быть null
    val publisher: String? = null, // Добавим недостающие поля
    val description: String? = null,
    val shelfPosition: String? = "Не указано",
    val isbn: String? = null,
    val shelfName: String? = null, // Название полки, к которой принадлежит книга
    val shelfLocation: String? = null, // Расположение (позиция) шкафа, к которой принадлежит книга
    val shelfNumber: String? = null, // Номер полки
    val placeNumber: String? = null // Номер места на полке
) : Parcelable