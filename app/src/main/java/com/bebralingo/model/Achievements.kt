package com.bebralingo.model

/**
 * Достижение — короткая цель с иконкой-эмодзи для витрины на экране спринтов.
 */
data class Achievement(
    val id: String,
    val emoji: String,
    val title: String,
    val description: String
)

/**
 * Совокупный локальный прогресс пользователя. Хранится в DataStore.
 */
data class UserProgress(
    val xp: Xp = 0,
    val streak: StreakDays = 0,
    /** ISO-дата (yyyy-MM-dd) последней тренировки, для расчёта серии. */
    val lastPracticeDate: String = "",
    val completedLessonIds: Set<LessonId> = emptySet(),
    /** Лучшая скорость (CPM) по каждому пройденному уроку. */
    val lessonBestCpm: Map<LessonId, Cpm> = emptyMap(),
    val bestSprintCpm: Cpm = 0,
    val unlockedAchievements: Set<String> = emptySet()
)

/**
 * Каталог всех достижений приложения. Разблокировка вычисляется
 * в [com.bebralingo.data.AppDataStore] при сохранении результатов.
 */
object Achievements {
    val ALL: List<Achievement> = listOf(
        Achievement(
            id = "speed_150",
            emoji = "🚀",
            title = "Ракета",
            description = "Набрать 150 зн/мин в любом режиме"
        ),
        Achievement(
            id = "flawless",
            emoji = "💎",
            title = "Безупречно",
            description = "Пройти спринт без единой ошибки"
        ),
        Achievement(
            id = "streak_7",
            emoji = "🔥",
            title = "Неделя огня",
            description = "7 дней серии подряд"
        ),
        Achievement(
            id = "first_lesson",
            emoji = "🌱",
            title = "Первый шаг",
            description = "Пройти первый урок"
        ),
        Achievement(
            id = "home_row_done",
            emoji = "🏠",
            title = "Основной ряд",
            description = "Пройти все уроки основного ряда"
        ),
        Achievement(
            id = "speed_300",
            emoji = "⚡",
            title = "Молния",
            description = "Набрать 300 зн/мин"
        )
    )
}
