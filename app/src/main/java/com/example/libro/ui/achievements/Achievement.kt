package com.example.libro.ui.achievements

data class Achievement(
    val title: String,
    val description: String,
    val progress: Int,
    val goal: Int,
    val isCompleted: Boolean
)
