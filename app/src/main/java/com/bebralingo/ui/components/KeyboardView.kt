package com.bebralingo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bebralingo.ui.theme.Blue
import com.bebralingo.ui.theme.Green
import com.bebralingo.ui.theme.Purple

/**
 * Зона большого пальца на экранной клавиатуре телефона.
 * На телефоне печатают двумя большими пальцами, а не всеми десятью.
 */
enum class ThumbZone(val color: Color) {
    /** Левая половина клавиатуры — левый большой палец. */
    Left(Blue),

    /** Правая половина — правый большой палец. */
    Right(Purple),

    /** Пробел — любым удобным пальцем. */
    Both(Green)
}

/**
 * Клавиша в схеме экранной клавиатуры.
 *
 * @param letter подпись клавиши
 * @param zone зона большого пальца — определяет цвет
 * @param fontSize размер шрифта подписи
 * @param minWidth минимальная ширина клавиши
 */
@Composable
fun GuideKey(
    letter: String,
    zone: ThumbZone = ThumbZone.Both,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 15.sp,
    minWidth: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = minWidth, minHeight = 44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(zone.color.copy(alpha = 0.18f))
            .border(1.5.dp, zone.color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            color = zone.color,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

/**
 * Раскладка экранной клавиатуры телефона ЙЦУКЕН.
 * Модификаторы физической клавиатуры (Ctrl, Alt, Caps, Tab) сюда не входят:
 * на телефоне их нет.
 */
object MobileKeyboard {

    /** Три буквенных ряда сверху вниз. */
    val letterRows: List<String> = listOf(
        "йцукенгшщзхъ",
        "фывапролджэ",
        "ячсмитьбю"
    )

    /** Нижний служебный ряд. */
    val bottomRow: List<String> = listOf("?123", "пробел", "⌫")

    /** Левая половина ряда — левый палец, правая — правый. */
    fun zoneOf(index: Int, rowSize: Int): ThumbZone =
        if (index < rowSize / 2) ThumbZone.Left else ThumbZone.Right

    /** Зона служебных клавиш нижнего ряда. */
    fun bottomZone(key: String): ThumbZone = when (key) {
        "?123" -> ThumbZone.Left
        "⌫" -> ThumbZone.Right
        else -> ThumbZone.Both
    }
}
