package com.example.libro.ui.achievements

import com.example.libro.Book
import com.example.libro.data.JsonHelper
import com.example.libro.ui.library.Shelf
import android.content.Context
import java.util.Calendar

object AchievementCalculator {
    
    fun calculateAllAchievements(context: Context): List<Achievement> {
        val books = JsonHelper.loadBooks(context)
        val shelves = JsonHelper.loadShelves(context)
        
        return AchievementType.values().map { type ->
            val progress = calculateProgress(type, books, shelves)
            Achievement(
                title = type.title,
                description = type.description,
                progress = progress,
                goal = type.goal,
                iconResId = type.iconResId,
                progressColorResId = type.progressColorResId,
                iconBackgroundColorResId = type.iconBackgroundColorResId
            )
        }
    }
    
    private fun calculateProgress(
        type: AchievementType,
        books: List<Book>,
        shelves: List<Shelf>
    ): Int {
        return when (type) {
            AchievementType.FIRST_READER -> {
                books.count { it.status.equals("Прочитана", ignoreCase = true) }.coerceAtLeast(0)
            }
            AchievementType.MARATHON_RUNNER -> {
                books.count { it.status.equals("Прочитана", ignoreCase = true) }
            }
            AchievementType.BOOKWORM -> {
                books.count { it.status.equals("Прочитана", ignoreCase = true) }
            }
            AchievementType.READING_MASTER -> {
                books.count { it.status.equals("Прочитана", ignoreCase = true) }
            }
            AchievementType.COLLECTOR -> {
                shelves.size
            }
            AchievementType.EXPLORER -> {
                val readBooks = books.filter { it.status.equals("Прочитана", ignoreCase = true) }
                val uniqueGenres = readBooks.flatMap { it.tags }.distinct().size
                uniqueGenres
            }
            AchievementType.RATING_MASTER -> {
                books.count { it.rating > 0 }
            }
            AchievementType.FAST_READER -> {
                // Подсчитываем книги, прочитанные за текущий месяц
                val calendar = Calendar.getInstance()
                val currentMonth = calendar.get(Calendar.MONTH)
                val currentYear = calendar.get(Calendar.YEAR)
                
                // Для упрощения считаем все прочитанные книги
                // В реальном приложении можно добавить дату прочтения в модель Book
                books.count { it.status.equals("Прочитана", ignoreCase = true) }
            }
        }
    }
}

