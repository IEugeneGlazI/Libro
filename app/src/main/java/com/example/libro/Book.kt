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
    val publisher: String? = "АСТ", // Добавим недостающие поля
    val description: String? = "Роман-эпопея Льва Николаевича Толстого, описывающий русское общество в эпоху войн против Наполеона в 1805—1812 годах.",
    val shelfPosition: String? = "Полка: 2  Место: 5",
    val isbn: String? = "978-5-17-098765-4",
    val shelfName: String? = null, // Название полки, к которой принадлежит книга
    val shelfLocation: String? = null, // Расположение (позиция) шкафа, к которой принадлежит книга
    val shelfNumber: String? = null, // Номер полки
    val placeNumber: String? = null // Номер места на полке
) : Parcelable