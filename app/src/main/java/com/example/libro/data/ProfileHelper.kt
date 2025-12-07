package com.example.libro.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object ProfileHelper {
    
    private const val PROFILE_FILE_NAME = "user_profile.json"

    fun saveProfile(context: Context, profile: UserProfile) {
        val json = Json.encodeToString(profile)
        val file = File(context.filesDir, PROFILE_FILE_NAME)
        file.writeText(json)
    }

    fun loadProfile(context: Context): UserProfile {
        val file = File(context.filesDir, PROFILE_FILE_NAME)
        return if (file.exists()) {
            val json = file.readText()
            Json.decodeFromString<UserProfile>(json)
        } else {
            UserProfile() // Возвращаем профиль по умолчанию
        }
    }
}

