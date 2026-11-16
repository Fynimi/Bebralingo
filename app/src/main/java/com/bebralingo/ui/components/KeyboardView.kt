package com.bebralingo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bebralingo.ui.theme.*
import com.bebralingo.ui.theme.Dim

/**
 * Клавиша на раскладке клавиатуры для экрана гида.
 *
 * @param letter символ на клавише
 * @param zone зона пальцев — определяет цвет
 * @param isHomeRowKey — клавиша домашней позиции (А/О)
 * @param fontSize размер шрифта
 */
@Composable
fun GuideKey(
    letter: String,
    zone: FingerZone = FingerZone.None,
    isHomeRowKey: Boolean = false,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 15.sp
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = modifier
                .defaultMinSize(minWidth = 40.dp, minHeight = 44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(zone.color.copy(0.18f))
                .border(1.5.dp, zone.color.copy(0.4f), RoundedCornerShape(10.dp))
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = zone.color,
                textAlign = TextAlign.Center
            )
        }
        if (isHomeRowKey) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "▲", fontSize = 10.sp, color = zone.color)
        }
    }
}

/**
 * Цветовая зона пальца для раскладки клавиатуры.
 */
enum class FingerZone(val color: Color) {
    LeftPinky(FingerPink),
    LeftRing(FingerRing),
    LeftMiddle(FingerMiddle),
    LeftIndex(FingerIndex),
    RightIndex(FingerIndex),
    RightMiddle(FingerMiddle),
    RightRing(FingerRing),
    RightPinky(FingerPink),
    Thumb(FingerThumb),
    None(FingerNone)
}

/**
 * Карта символов → зона пальцев для ЙЦУКЕН раскладки.
 * Используется на экране гида и на экране тренировки.
 */
object FingerMap {

    // Левый мизинец
    private val leftPinky = setOf('ё', 'й', 'ф', 'я')
    // Левый безымянный
    private val leftRing = setOf('ц', 'ы', 'ч')
    // Левый средний
    private val leftMiddle = setOf('у', 'в', 'с')
    // Левый указательный
    private val leftIndex = setOf('к', 'е', 'а', 'п', 'м', 'и')
    // Правый указательный
    private val rightIndex = setOf('н', 'г', 'о', 'р', 'т', 'ь')
    // Правый средний
    private val rightMiddle = setOf('ш', 'щ', 'л', 'б')
    // Правый безымянный
    private val rightRing = setOf('з', 'ж', 'д', 'ю')
    // Правый мизинец
    private val rightPinky = setOf('х', 'ъ', 'э', '`', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '=', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '+', '[', ']', '{', '}', '|', ':', '"', '<', '>', '?')

    fun getZone(char: Char): FingerZone = when {
        char == ' ' -> FingerZone.Thumb
        char.lowercaseChar() in leftPinky -> FingerZone.LeftPinky
        char.lowercaseChar() in leftRing -> FingerZone.LeftRing
        char.lowercaseChar() in leftMiddle -> FingerZone.LeftMiddle
        char.lowercaseChar() in leftIndex -> FingerZone.LeftIndex
        char.lowercaseChar() in rightIndex -> FingerZone.RightIndex
        char.lowercaseChar() in rightMiddle -> FingerZone.RightMiddle
        char.lowercaseChar() in rightRing -> FingerZone.RightRing
        char.lowercaseChar() in rightPinky -> FingerZone.RightPinky
        else -> FingerZone.None
    }

    /** Раскладка по рядам (верхний → нижний). */
    val rows: List<List<String>> = listOf(
        listOf("`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "Backspace"),
        listOf("Tab", "й", "ц", "у", "к", "е", "н", "г", "ш", "щ", "з", "х", "ъ"),
        listOf("Caps", "ф", "ы", "в", "а", "п", "р", "о", "л", "д", "ж", "э"),
        listOf("Shift", "я", "ч", "с", "м", "и", "т", "ь", "б", "ю", ".", "Shift"),
        listOf("Ctrl", "Win", "Alt", " ", "Alt", "Fn", "Menu", "Ctrl")
    )

    /** Для домашней позиции — клавиши А и О (с насечками). */
    val homeRowKeys = setOf("а", "о")
}
