package com.example.libro.data

import android.content.Context
import com.example.libro.Book
import com.example.libro.ui.library.Shelf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object JsonHelper {

    private const val SHELVES_FILE_NAME = "shelves.json"
    private const val BOOKS_FILE_NAME = "books.json"

    fun saveShelves(context: Context, shelves: List<Shelf>) {
        val json = Json.encodeToString(shelves)
        val file = File(context.filesDir, SHELVES_FILE_NAME)
        file.writeText(json)
    }

    fun loadShelves(context: Context): MutableList<Shelf> {
        val file = File(context.filesDir, SHELVES_FILE_NAME)
        return if (file.exists()) {
            val json = file.readText()
            Json.decodeFromString<MutableList<Shelf>>(json)
        } else {
            mutableListOf()
        }
    }

    fun saveBooks(context: Context, books: List<Book>) {
        val json = Json.encodeToString(books)
        val file = File(context.filesDir, BOOKS_FILE_NAME)
        file.writeText(json)
    }

    fun loadBooks(context: Context): MutableList<Book> {
        val file = File(context.filesDir, BOOKS_FILE_NAME)
        return if (file.exists()) {
            val json = file.readText()
            Json.decodeFromString<MutableList<Book>>(json)
        } else {
            mutableListOf()
        }
    }
}
