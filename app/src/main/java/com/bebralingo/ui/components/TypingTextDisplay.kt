package com.bebralingo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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

/**
 * Целевой текст для набора с посимвольной подсветкой.
 *
 * Ширина строки не задаётся числом символов: слова переносятся по фактической
 * ширине экрана через [FlowRow], поэтому текст никогда не уезжает за границы,
 * а на узких экранах и при увеличенном системном шрифте размер символов
 * уменьшается автоматически.
 *
 * @param target текст, который нужно напечатать
 * @param typed уже введённый текст
 * @param fontSize базовый размер символа
 * @param cellPadding горизонтальный отступ внутри ячейки символа
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TypingTextDisplay(
    target: String,
    typed: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 24.sp,
    cellPadding: Dp = 4.dp
) {
    val words = remember(target) { splitWords(target) }

    BoxWithConstraints(modifier = modifier) {
        val compact = maxWidth < 340.dp
        val adaptedFontSize = if (compact) fontSize * 0.85f else fontSize
        val adaptedPadding = if (compact) 3.dp else cellPadding

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center
        ) {
            words.forEach { word ->
                Row {
                    word.text.forEachIndexed { offset, char ->
                        TypingChar(
                            char = char,
                            globalIndex = word.startIndex + offset,
                            typed = typed,
                            fontSize = adaptedFontSize,
                            cellPadding = adaptedPadding
                        )
                    }
                }
            }
        }
    }
}

/**
 * Слово с позицией первого символа в общем тексте.
 * Пробел после слова входит в это же слово, чтобы перенос не отрывал его.
 */
private data class WordChunk(val text: String, val startIndex: Int)

private fun splitWords(target: String): List<WordChunk> {
    if (target.isEmpty()) return emptyList()

    val chunks = mutableListOf<WordChunk>()
    var index = 0
    var start = 0

    while (index <= target.length) {
        val isEnd = index == target.length
        val isSpace = !isEnd && target[index] == ' '

        if (isEnd || isSpace) {
            val text = target.substring(start, if (isSpace) index + 1 else index)
            if (text.isNotEmpty()) chunks += WordChunk(text, start)
            start = index + 1
        }
        index++
    }
    return chunks
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
