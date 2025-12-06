package com.example.libro.data

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object ImageHelper {
    
    private const val COVERS_DIR = "book_covers"
    
    /**
     * Копирует изображение из URI в постоянное хранилище приложения
     * @return Путь к сохраненному файлу с префиксом file:// или null в случае ошибки
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri, bookId: String? = null): String? {
        return try {
            val coversDir = File(context.filesDir, COVERS_DIR)
            if (!coversDir.exists()) {
                coversDir.mkdirs()
            }
            
            // Генерируем уникальное имя файла
            val fileName = bookId ?: System.currentTimeMillis().toString()
            val imageFile = File(coversDir, "cover_$fileName.jpg")
            
            // Копируем файл
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(imageFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            
            // Возвращаем путь к файлу с префиксом file:// для Coil
            "file://${imageFile.absolutePath}"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Проверяет, является ли URL локальным файлом
     */
    fun isLocalFile(url: String?): Boolean {
        return url != null && (url.startsWith("/") || url.startsWith("file://"))
    }
    
    /**
     * Удаляет старый файл изображения, если он существует
     */
    fun deleteImageFile(context: Context, filePath: String?) {
        if (filePath != null && isLocalFile(filePath)) {
            try {
                val cleanPath = filePath.removePrefix("file://")
                val file = File(cleanPath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

