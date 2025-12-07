package com.example.libro.ui.achievements

import com.example.libro.R

data class Achievement(
    val title: String,
    val description: String,
    val progress: Int,
    val goal: Int,
    val iconResId: Int,
    val progressColorResId: Int,
    val iconBackgroundColorResId: Int
) {
    val isCompleted: Boolean
        get() = progress >= goal
}

enum class AchievementType(
    val title: String,
    val description: String,
    val goal: Int,
    val iconResId: Int,
    val progressColorResId: Int,
    val iconBackgroundColorResId: Int
) {
    FIRST_READER(
        "Первый читатель",
        "Прочитайте первую книгу",
        1,
        R.drawable.ic_cup,
        R.color.achievement_first_reader,
        R.color.achievement_first_reader_light
    ),
    MARATHON_RUNNER(
        "Марафонец",
        "Прочитайте 5 книг",
        5,
        R.drawable.ic_fire,
        R.color.achievement_marathon_runner,
        R.color.achievement_marathon_runner_light
    ),
    BOOKWORM(
        "Книжный червь",
        "Прочитайте 10 книг",
        10,
        R.drawable.ic_book,
        R.color.achievement_bookworm,
        R.color.achievement_bookworm_light
    ),
    READING_MASTER(
        "Мастер чтения",
        "Прочитайте 25 книг",
        25,
        R.drawable.ic_graduation_cap,
        R.color.achievement_reading_master,
        R.color.achievement_reading_master_light
    ),
    COLLECTOR(
        "Коллекционер",
        "Создайте 5 шкафов",
        5,
        R.drawable.ic_shelf_books,
        R.color.achievement_collector,
        R.color.achievement_collector_light
    ),
    EXPLORER(
        "Исследователь",
        "Прочитайте книги из 3 разных жанров",
        3,
        R.drawable.ic_search,
        R.color.achievement_explorer,
        R.color.achievement_explorer_light
    ),
    RATING_MASTER(
        "Оценщик",
        "Оцените 10 книг",
        10,
        R.drawable.ic_star,
        R.color.achievement_rating_master,
        R.color.achievement_rating_master_light
    ),
    FAST_READER(
        "Скоростной читатель",
        "Прочитайте 3 книги за месяц",
        3,
        R.drawable.ic_bookmark,
        R.color.achievement_fast_reader,
        R.color.achievement_fast_reader_light
    )
}
