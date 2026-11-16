package com.bebralingo.ui.screens.sprints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bebralingo.data.AppDataStore
import com.bebralingo.data.LessonContent
import com.bebralingo.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Состояние экрана спринтов.
 */
data class SprintsState(
    val selectedMode: SprintMode? = null,
    val isRunning: Boolean = false,
    val remainingSeconds: Int = 0,
    val typedText: String = "",
    val currentText: String = "",
    val liveCpm: Int = 0,
    val errorCount: Int = 0,
    val correctCount: Int = 0,
    val totalTyped: Int = 0,
    val finished: Boolean = false,
    val result: TrainingResult? = null,
    val unlockedAchievements: List<Achievement> = emptyList()
)

/**
 * ViewModel экрана спринтов.
 * Управляет режимами 30с / 1мин / 3мин, таймером, многострочным вводом и подсчётом CPM.
 */
class SprintsViewModel(private val store: AppDataStore) : ViewModel() {

    private val _state = MutableStateFlow(SprintsState())
    val state: StateFlow<SprintsState> = _state

    /** Лучший CPM пользователя в спринтах. */
    val bestSprintCpm: StateFlow<Int> = store.progress
        .map { it.bestSprintCpm }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Полный прогресс — нужен, чтобы показывать, какие достижения уже открыты. */
    val userProgress: StateFlow<UserProgress> = store.progress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProgress())

    private var timerJob: Job? = null
    private var startTimeMs: Long = 0

    // Накопительные счетчики для нескольких текстов за один спринт
    private var accumulatedCorrect = 0
    private var accumulatedTyped = 0
    private var accumulatedErrors = 0

    val sprintModes: List<SprintMode> = listOf(
        SprintMode("30", "30 сек", 30),
        SprintMode("60", "1 минута", 60),
        SprintMode("180", "3 минуты", 180)
    )

    fun selectMode(mode: SprintMode) {
        _state.update {
            it.copy(
                selectedMode = mode,
                isRunning = false,
                finished = false,
                typedText = "",
                currentText = "",
                remainingSeconds = mode.seconds,
                liveCpm = 0,
                errorCount = 0,
                correctCount = 0,
                totalTyped = 0,
                result = null,
                unlockedAchievements = emptyList()
            )
        }
    }

    fun startSprint() {
        val mode = _state.value.selectedMode ?: return
        accumulatedCorrect = 0
        accumulatedTyped = 0
        accumulatedErrors = 0
        startTimeMs = System.currentTimeMillis()

        _state.update {
            it.copy(
                isRunning = true,
                finished = false,
                typedText = "",
                currentText = randomText(),
                remainingSeconds = mode.seconds,
                liveCpm = 0,
                errorCount = 0,
                correctCount = 0,
                totalTyped = 0,
                result = null,
                unlockedAchievements = emptyList()
            )
        }
        startTimer(mode.seconds)
    }

    private fun randomText(): String {
        return LessonContent.sprintTexts[Random.nextInt(LessonContent.sprintTexts.size)]
    }

    private fun startTimer(duration: Int) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            for (s in duration downTo 1) {
                _state.update { it.copy(remainingSeconds = s) }
                delay(1000)
            }
            endSprint()
        }
    }

    fun onTextChange(newText: String) {
        val state = _state.value
        if (!state.isRunning || state.finished) return

        val target = state.currentText
        val typed = newText.take(target.length)

        var currentSentenceErrors = 0
        var currentSentenceCorrect = 0
        for (i in typed.indices) {
            if (i < target.length) {
                if (typed[i] == target[i]) currentSentenceCorrect++
                else currentSentenceErrors++
            }
        }

        val totalCorrect = accumulatedCorrect + currentSentenceCorrect
        val totalErrors = accumulatedErrors + currentSentenceErrors
        val totalTyped = accumulatedTyped + typed.length

        val elapsedMs = maxOf(500L, System.currentTimeMillis() - startTimeMs)
        val elapsedMinutes = elapsedMs / 60000.0
        val cpm = if (elapsedMinutes > 0) (totalCorrect / elapsedMinutes).toInt() else 0

        // Если текст набран до конца (даже с ошибками) — переходим к следующему:
        // иначе на неверном последнем символе ввод упирается в ограничение длины
        if (typed.length >= target.length) {
            accumulatedCorrect += currentSentenceCorrect
            accumulatedTyped += typed.length
            accumulatedErrors += currentSentenceErrors

            _state.update {
                it.copy(
                    typedText = "",
                    currentText = randomText(),
                    errorCount = totalErrors,
                    correctCount = totalCorrect,
                    totalTyped = totalTyped,
                    liveCpm = cpm
                )
            }
        } else {
            _state.update {
                it.copy(
                    typedText = typed,
                    errorCount = totalErrors,
                    correctCount = totalCorrect,
                    totalTyped = totalTyped,
                    liveCpm = cpm
                )
            }
        }
    }

    private fun endSprint() {
        timerJob?.cancel()
        timerJob = null

        val state = _state.value
        if (!state.isRunning) return

        val modeSeconds = state.selectedMode?.seconds ?: 60
        val minutes = modeSeconds / 60.0
        val cpm = if (minutes > 0) (state.correctCount / minutes).toInt() else 0

        val totalTyped = maxOf(state.totalTyped, state.correctCount)
        val accuracy = if (totalTyped == 0) 100f
        else (state.correctCount * 100f / totalTyped).coerceIn(0f, 100f)
        val isPerfect = state.errorCount == 0 && state.correctCount > 0

        val result = TrainingResult(
            cpm = cpm,
            accuracy = accuracy,
            xpGained = computeXp(cpm, accuracy),
            errorCount = state.errorCount,
            isPerfect = isPerfect,
            durationSeconds = modeSeconds.toLong()
        )

        _state.update {
            it.copy(
                isRunning = false,
                finished = true,
                remainingSeconds = 0,
                liveCpm = cpm,
                result = result
            )
        }

        viewModelScope.launch {
            // Сохраняем всегда, когда что-то напечатано: иначе не растёт серия
            // дней и теряются достижения. Пустой спринт ничего не начисляет.
            if (state.totalTyped > 0) {
                val unlocked = store.recordSprint(result)
                if (unlocked.isNotEmpty()) {
                    _state.update { it.copy(unlockedAchievements = unlocked) }
                }
            }
        }
    }

    fun reset() {
        timerJob?.cancel()
        timerJob = null
        _state.update { SprintsState(selectedMode = it.selectedMode) }
    }

    private fun computeXp(cpm: Int, accuracy: Float): Int {
        if (cpm <= 0) return 0
        var xp = 10
        if (cpm >= 50) xp += 10
        if (cpm >= 100) xp += 15
        if (cpm >= 150) xp += 15
        if (accuracy >= 95) xp += 10
        return xp.coerceAtMost(60)
    }
}
