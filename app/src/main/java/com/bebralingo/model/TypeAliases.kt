package com.bebralingo.model

/**
 * Строковые типы-алиасы делают сигнатуры функций самодокументируемыми.
 * Все «денежные» единицы приложения (очки, скорость, точность) вынесены сюда,
 * чтобы не путать Int/Float между собой в параметрах.
 */

/** Опыт пользователя в очках XP. */
typealias Xp = Int

/** Скорость печати в знаках в минуту (CPM). */
typealias Cpm = Int

/** Точность в процентах, 0f..100f. */
typealias Accuracy = Float

/** Серия дней подряд (streak). */
typealias StreakDays = Int

/** Уникальный идентификатор урока или блока. */
typealias LessonId = String
typealias BlockId = String

/** Длительность спринта в секундах. */
typealias SprintSeconds = Int
