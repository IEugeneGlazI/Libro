package com.example.libro.data

import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val userName: String = "Пользователь",
    val profileImagePath: String? = null
)

