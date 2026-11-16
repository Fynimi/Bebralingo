package com.bebralingo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bebralingo.ui.theme.Green
import com.bebralingo.ui.theme.GreenDark
import com.bebralingo.ui.theme.Orange
import com.bebralingo.ui.theme.Red
import com.bebralingo.ui.theme.RedDark
import com.bebralingo.ui.theme.TextLight
import com.bebralingo.ui.theme.TextPrimary
import kotlin.math.max

/**
 * Целевой текст для набора с посимвольной подсветкой.
 *
 * Размер шрифта постоянный; число символов в строке вычисляется по фактической
 * ширине карточки и реальной ширине моноширинного символа, поэтому текст
 * не выходит за границы экрана и не «прыгает» при смене устройства.
 * Перенос — по словам, слишком длинное слово дробится по символам.
 *
 * @param target текст, который нужно напечатать
 * @param typed уже введённый текст
 * @param fontSize размер символа
 * @param cellPadding горизонтальный отступ внутри ячейки символа
 */
@Composable
fun TypingTextDisplay(
    target: String,
    typed: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    cellPadding: Dp = 4.dp
) {
    if (target.isEmpty()) return

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val measurer = rememberTextMeasurer()

        val sampleStyle = TextStyle(
            fontSize = fontSize,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
        )

        val maxChars = remember(fontSize, cellPadding, constraints.maxWidth, density) {
            val charWidthPx = measurer.measure("Ш", sampleStyle).size.width.toFloat()
            val paddingsPx = with(density) { (4.dp + cellPadding * 2).toPx() }
            val cellWidthPx = charWidthPx + paddingsPx
            if (constraints.maxWidth == Constraints.Infinity || cellWidthPx <= 0f) {
                24
            } else {
                max(4, (constraints.maxWidth / cellWidthPx).toInt())
            }
        }

        val lines = remember(target, maxChars) { wrapIndices(target, maxChars) }

        Column(modifier = Modifier.fillMaxWidth()) {
            lines.forEach { range ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (index in range) {
                        TypingChar(
                            char = target[index],
                            globalIndex = index,
                            typed = typed,
                            fontSize = fontSize,
                            cellPadding = cellPadding
                        )
                    }
                }
            }
        }
    }
}

/**
 * Разбивает текст на строки по [maxChars] символов, стараясь резать по пробелу.
 * Возвращает диапазоны индексов символов исходной строки.
 */
private fun wrapIndices(target: String, maxChars: Int): List<IntRange> {
    if (maxChars <= 0) return listOf(target.indices)

    val lines = mutableListOf<IntRange>()
    var start = 0
    while (start < target.length) {
        var end = minOf(start + maxChars, target.length)
        if (end < target.length) {
            val lastSpace = target.lastIndexOf(' ', end - 1)
            if (lastSpace >= start) {
                end = lastSpace + 1
            }
        }
        lines += start until end
        start = end
    }
    return lines
}

@Composable
private fun TypingChar(
    char: Char,
    globalIndex: Int,
    typed: String,
    fontSize: TextUnit,
    cellPadding: Dp
) {
    val isTyped = globalIndex < typed.length
    val isCorrect = isTyped && typed[globalIndex] == char
    val isCurrent = globalIndex == typed.length

    val bgColor = when {
        isTyped && isCorrect -> Green.copy(alpha = 0.22f)
        isTyped && !isCorrect -> Red.copy(alpha = 0.25f)
        isCurrent -> Orange.copy(alpha = 0.35f)
        else -> Color.Transparent
    }

    val textColor = when {
        isTyped && isCorrect -> GreenDark
        isTyped && !isCorrect -> RedDark
        isCurrent -> TextPrimary
        else -> TextLight
    }

    val borderModifier = if (isCurrent) {
        Modifier.border(2.dp, Orange, RoundedCornerShape(6.dp))
    } else Modifier

    Box(
        modifier = Modifier
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .then(borderModifier)
            .padding(horizontal = cellPadding, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (char == ' ') "␣" else char.toString(),
            fontSize = fontSize,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1
        )
    }
}
