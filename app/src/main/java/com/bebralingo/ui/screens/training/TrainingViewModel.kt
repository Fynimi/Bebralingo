package com.bebralingo.ui.screens.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bebralingo.data.AppDataStore
import com.bebralingo.data.LessonContent
import com.bebralingo.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Состояние экрана тренировки.
 */
data class TrainingState(
    val lessonId: String = "",
    val lessonTitle: String = "",
    val target: String = "",
    val typed: String = "",
    val errorCount: Int = 0,
    val correctCount: Int = 0,
    val totalTyped: Int = 0,
    val started: Boolean = false,
    val finished: Boolean = false,
    val result: TrainingResult? = null,
    val liveStats: LiveStats = LiveStats(),
    /** Счётчик ошибок: каждое новое значение перезапускает анимацию тряски. */
    val shakeTick: Int = 0
)

/**
 * ViewModel тренировочного экрана.
 * Отслеживает ввод, считает статистику в реальном времени,
 * фиксирует ошибки с тряской и вибрацией.
 */
class TrainingViewModel(
    private val store: AppDataStore,
    val lessonId: String
) : ViewModel() {

    private val _state = MutableStateFlow(TrainingState())
    val state: StateFlow<TrainingState> = _state

    private val lesson = LessonContent.findLesson(lessonId)
    private var startTime: Long = 0

    init {
        _state.update {
            it.copy(
                lessonId = lessonId,
                lessonTitle = lesson?.title ?: "",
                target = lesson?.targetText ?: ""
            )
        }
    }

    /** Начать тренировку. */
    fun start() {
        if (_state.value.started) return
        startTime = System.currentTimeMillis()
        _state.update { it.copy(started = true) }
    }

    /**
     * Обработка изменения ввода.
     * Сравниваем с целевым текстом, считаем ошибки и правильные символы,
     * а при неверном вводе вызываем shake-эффект.
     */
    fun onTextChange(newText: String) {
        val currentState = _state.value
        if (currentState.finished) return

        if (!currentState.started && newText.isNotEmpty()) {
            start()
        }

        val target = currentState.target
        val typed = newText.take(target.length)

        // Эффект вибрации/тряски при ошибке ввода
        if (typed.length > currentState.typed.length) {
            val newIdx = typed.length - 1
            if (newIdx < target.length && typed[newIdx] != target[newIdx]) {
                triggerShake()
            }
        }

        var errors = 0
        var correct = 0
        for (i in typed.indices) {
            if (i < target.length) {
                if (typed[i] == target[i]) {
                    correct++
                } else {
                    errors++
                }
            }
        }

        val addedChars = maxOf(0, typed.length - currentState.typed.length)
        val newTotalTyped = currentState.totalTyped + addedChars

        val cpm = computeCpm(correct)
        val accuracy = computeAccuracy(correct, newTotalTyped)

        _state.update {
            it.copy(
                typed = typed,
                errorCount = errors,
                correctCount = correct,
                totalTyped = newTotalTyped,
                liveStats = LiveStats(
                    cpm = cpm,
                    accuracy = accuracy,
                    typedChars = typed.length,
                    errorCount = errors
                )
            )
        }

        if (typed.length >= target.length && typed == target) {
            finish()
        }
    }

    private fun computeCpm(correctChars: Int): Int {
        if (startTime == 0L) return 0
        val elapsedMs = System.currentTimeMillis() - startTime
        if (elapsedMs < 500) return 0
        val minutes = elapsedMs / 60000.0
        return (correctChars / minutes).toInt()
    }

    private fun computeAccuracy(correct: Int, total: Int): Float {
        if (total == 0) return 100f
        return (correct * 100f / total).coerceIn(0f, 100f)
    }

    /**
     * Завершить тренировку.
     * Сохраняет прогресс, XP, рассчитывает итоговый результат.
     */
    fun finish() {
        val currentState = _state.value
        if (currentState.finished) return

        val elapsedMs = maxOf(1000L, System.currentTimeMillis() - startTime)
        val minutes = elapsedMs / 60000.0
        val cpm = if (minutes > 0) (currentState.correctCount / minutes).toInt() else 0

        val accuracy = computeAccuracy(currentState.correctCount, currentState.totalTyped)
        val xp = computeXp(cpm, accuracy)

        val result = TrainingResult(
            cpm = cpm,
            accuracy = accuracy,
            xpGained = xp,
            errorCount = currentState.errorCount,
            isPerfect = currentState.errorCount == 0 && currentState.totalTyped > 0,
            durationSeconds = elapsedMs / 1000
        )

        viewModelScope.launch {
            store.completeLesson(lessonId, result)
        }

        _state.update {
            it.copy(
                finished = true,
                result = result
            )
        }
    }

    private fun computeXp(cpm: Int, accuracy: Float): Int {
        var xp = 15
        if (cpm >= 30) xp += 5
        if (cpm >= 60) xp += 10
        if (cpm >= 100) xp += 15
        if (accuracy >= 95) xp += 5
        return xp.coerceAtMost(50)
    }

    /** Вызвать вибрацию и тряску при ошибке. */
    fun triggerShake() {
        _state.update { it.copy(shakeTick = it.shakeTick + 1) }
    }
}
