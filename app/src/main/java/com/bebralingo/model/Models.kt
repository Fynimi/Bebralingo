package com.bebralingo.model

import androidx.compose.ui.graphics.Color

/**
 * Статус урока на карте пути. Определяет внешний вид узла и доступность.
 */
enum class LessonStatus {
    /** Пользователь уже прошёл урок успешно. */
    COMPLETED,

    /** Текущий урок — в него можно войти. */
    CURRENT,

    /** Урок заблокирован, пока не пройден предыдущий. */
    LOCKED
}

/**
 * Один уровень (урок) на карте. [targetText] — то, что нужно напечатать.
 * [focusKeys] — набор клавиш, которые отрабатываются (для подсказок и гида).
 */
data class Lesson(
    val id: LessonId,
    val blockId: BlockId,
    val title: String,
    val subtitle: String,
    val targetText: String,
    val focusKeys: String
)

/**
 * Блок уроков = группа уровней вокруг одной темы (ряд клавиатуры и т.п.).
 * [accent] — цвет-акцент блока для карточки и узла перехода.
 */
data class LessonBlock(
    val id: BlockId,
    val title: String,
    val description: String,
    val accent: Color,
    val lessons: List<Lesson>
)

/**
 * Живые метрики текущей тренировки (обновляются на каждое нажатие).
 */
data class LiveStats(
    val cpm: Cpm = 0,
    val accuracy: Accuracy = 100f,
    val typedChars: Int = 0,
    val errorCount: Int = 0
)

/**
 * Итог завершённого урока / спринта.
 */
data class TrainingResult(
    val cpm: Cpm,
    val accuracy: Accuracy,
    val xpGained: Xp,
    val errorCount: Int,
    val isPerfect: Boolean,
    val durationSeconds: Long
)

/**
 * Режим спринта: фиксированная длительность + набор случайных текстов.
 */
data class SprintMode(
    val id: String,
    val label: String,
    val seconds: SprintSeconds
)

/**
 * Рекорд и достижения пользователя в спринтах.
 */
data class SprintRecord(
    val bestCpm: Cpm = 0,
    val bestModeId: String = ""
)
