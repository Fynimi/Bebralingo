package com.bebralingo.data

import com.bebralingo.model.*

/**
 * Вычисляет, какие достижения следуют разблокировать по текущему прогрессу
 * и последнему результату тренировки.
 * Вызывается из [AppDataStore] после сохранения урока или спринта.
 */
object AchievementEvaluator {

    private const val HOME_ROW_BLOCK_ID = "b1"

    /**
     * @param progress прогресс уже после применения нового результата
     * @param result итог урока/спринта, если он есть (для «скоростных» наград)
     * @param sprintResult true, если [result] получен в спринте: награда «Безупречно»
     *   выдаётся только за спринт без ошибок, как и описано в каталоге
     * @return достижения, которые открылись именно сейчас
     */
    fun evaluate(
        progress: UserProgress,
        result: TrainingResult? = null,
        sprintResult: Boolean = false
    ): List<Achievement> {
        val earned = mutableSetOf<String>()

        if (progress.completedLessonIds.isNotEmpty()) {
            earned += "first_lesson"
        }

        val homeRow = LessonContent.blocks.firstOrNull { it.id == HOME_ROW_BLOCK_ID }
        if (homeRow != null && homeRow.lessons.all { it.id in progress.completedLessonIds }) {
            earned += "home_row_done"
        }

        if (progress.streak >= 7) {
            earned += "streak_7"
        }

        if (progress.bestSprintCpm >= 150) {
            earned += "speed_150"
        }
        if (progress.bestSprintCpm >= 300) {
            earned += "speed_300"
        }

        if (result != null) {
            if (result.cpm >= 150) earned += "speed_150"
            if (result.cpm >= 300) earned += "speed_300"
            if (sprintResult && result.isPerfect && result.errorCount == 0) earned += "flawless"
        }

        val alreadyUnlocked = progress.unlockedAchievements
        return Achievements.ALL.filter { it.id in earned && it.id !in alreadyUnlocked }
    }
}
