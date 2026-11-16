package com.bebralingo.ui.screens.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bebralingo.data.AppDataStore
import com.bebralingo.data.LessonContent
import com.bebralingo.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана карты уроков.
 * Вычисляет статус каждого урока (completed / current / locked)
 * на основе прогресса пользователя.
 */
class MapViewModel(private val store: AppDataStore) : ViewModel() {

    val progress: StateFlow<UserProgress> = store.progress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProgress())

    val blocks: List<LessonBlock> = LessonContent.blocks

    /** Плоский список всех уроков по порядку пути. */
    private val allLessons: List<Lesson> = LessonContent.allLessons

    /**
     * Статус конкретного урока.
     * COMPLETED — пройден.
     * CURRENT   — следующий доступный урок.
     * LOCKED    — заблокирован.
     */
    fun getLessonStatus(lessonId: String): LessonStatus {
        val completed = progress.value.completedLessonIds
        if (lessonId in completed) return LessonStatus.COMPLETED

        val idx = allLessons.indexOfFirst { it.id == lessonId }
        if (idx < 0) return LessonStatus.LOCKED

        // Первый урок всегда доступен
        if (idx == 0) return LessonStatus.CURRENT

        // Доступен, если предыдущий урок пройден
        return if (allLessons[idx - 1].id in completed) {
            LessonStatus.CURRENT
        } else {
            LessonStatus.LOCKED
        }
    }

    /**
     * Первый непройденный доступный урок (для кнопки «Продолжить»).
     */
    fun getCurrentLessonId(): LessonId? {
        return allLessons.find { getLessonStatus(it.id) == LessonStatus.CURRENT }?.id
    }

    /** Сколько уроков пройдено в блоке. */
    fun completedInBlock(blockId: String): Int {
        val completed = progress.value.completedLessonIds
        return allLessons.count { it.blockId == blockId && it.id in completed }
    }

    /** Всего уроков в блоке. */
    fun totalInBlock(blockId: String): Int {
        return allLessons.count { it.blockId == blockId }
    }
}
