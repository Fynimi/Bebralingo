package com.bebralingo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.bebralingo.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "bebralingo")

/**
 * Обёртка над DataStore Preferences.
 * Хранит: XP, streak, серии, прогресс уроков, рекорды спринтов, достижения.
 */
class AppDataStore(private val context: Context) {

    private val prefs = context.dataStore

    /** Скоуп для кэширования прогресса; живёт столько же, сколько хранилище. */
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // --- Keys ---

    private val keyXp = intPreferencesKey("xp")
    private val keyStreak = intPreferencesKey("streak")
    private val keyLastDate = stringPreferencesKey("last_date")
    private val keyCompleted = stringSetPreferencesKey("completed")
    private val keyBestCpm = stringSetPreferencesKey("best_cpm")
    private val keyBestSprint = intPreferencesKey("best_sprint")
    private val keyAchievements = stringSetPreferencesKey("achievements")

    // --- Read ---

    val progress: Flow<UserProgress> = prefs.data
        .catch { emit(emptyPreferences()) }
        .map { toProgress(it) }

    /** Кэш последнего состояния для синхронного чтения. */
    @Volatile
    var currentProgress: UserProgress = UserProgress()
        private set

    init {
        scope.launch {
            progress.collect { currentProgress = it }
        }
    }

    private fun toProgress(p: Preferences): UserProgress = UserProgress(
        xp = p[keyXp] ?: 0,
        streak = p[keyStreak] ?: 0,
        lastPracticeDate = p[keyLastDate] ?: "",
        completedLessonIds = p[keyCompleted] ?: emptySet(),
        lessonBestCpm = parseCpmMap(p[keyBestCpm] ?: emptySet()),
        bestSprintCpm = p[keyBestSprint] ?: 0,
        unlockedAchievements = p[keyAchievements] ?: emptySet()
    )

    private fun parseCpmMap(entries: Set<String>): Map<LessonId, Cpm> = buildMap {
        entries.forEach { entry ->
            val separator = entry.lastIndexOf('=')
            if (separator <= 0) return@forEach
            val id = entry.substring(0, separator)
            val value = entry.substring(separator + 1).toIntOrNull() ?: return@forEach
            put(id, value)
        }
    }

    // --- Write ---

    /**
     * Отметить урок пройденным.
     * Начисляет XP, обновляет streak и лучший CPM урока,
     * затем разблокирует подходящие достижения.
     *
     * @return список только что открытых достижений.
     */
    suspend fun completeLesson(lessonId: LessonId, result: TrainingResult): List<Achievement> {
        val edited = prefs.edit { p ->
            p[keyXp] = (p[keyXp] ?: 0) + result.xpGained

            val completed = (p[keyCompleted] ?: emptySet()).toMutableSet()
            completed += lessonId
            p[keyCompleted] = completed

            val bestEntries = (p[keyBestCpm] ?: emptySet()).toMutableSet()
            val existingBest = parseCpmMap(bestEntries)[lessonId] ?: 0
            if (result.cpm > existingBest) {
                bestEntries -= "$lessonId=$existingBest"
                bestEntries += "$lessonId=${result.cpm}"
                p[keyBestCpm] = bestEntries
            }

            updateStreak(p)
        }
        return commit(edited, result)
    }

    /**
     * Записать результат спринта: XP, личный рекорд, серия и достижения.
     * Сохраняется всегда, даже если рекорд не побит, — иначе теряются
     * серия дней и достижения за точность.
     *
     * @return список только что открытых достижений.
     */
    suspend fun recordSprint(result: TrainingResult): List<Achievement> {
        val edited = prefs.edit { p ->
            p[keyXp] = (p[keyXp] ?: 0) + result.xpGained
            if (result.cpm > (p[keyBestSprint] ?: 0)) {
                p[keyBestSprint] = result.cpm
            }
            updateStreak(p)
        }
        return commit(edited, result, sprintResult = true)
    }

    /**
     * Сохраняет достижения, полученные по итогам [result], и обновляет кэш.
     */
    private suspend fun commit(
        edited: Preferences,
        result: TrainingResult,
        sprintResult: Boolean = false
    ): List<Achievement> {
        val after = toProgress(edited)
        val unlocked = AchievementEvaluator.evaluate(after, result, sprintResult)
        if (unlocked.isEmpty()) {
            currentProgress = after
            return emptyList()
        }

        val withAchievements = prefs.edit { p ->
            val achievements = (p[keyAchievements] ?: emptySet()).toMutableSet()
            unlocked.forEach { achievements += it.id }
            p[keyAchievements] = achievements
        }
        currentProgress = toProgress(withAchievements)
        return unlocked
    }

    private fun updateStreak(p: MutablePreferences) {
        val today = LocalDate.now().toString()
        val yesterday = LocalDate.now().minusDays(1).toString()
        val last = p[keyLastDate] ?: ""
        val current = p[keyStreak] ?: 0

        p[keyLastDate] = today
        p[keyStreak] = when {
            last == today -> maxOf(current, 1)
            last == yesterday -> current + 1
            else -> 1
        }
    }
}
