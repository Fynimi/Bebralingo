package com.bebralingo.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bebralingo.ui.theme.*
import com.bebralingo.ui.theme.Dim

/**
 * Скруглённый прогресс-бар с внешней обводкой.
 * Заполняется от 0f до 1f, показывает зелёный «достигнуто»
 * и серый «оставшееся».
 *
 * @param progress от 0 до 1
 * @param height высота полоски
 * @param borderColor цвет рамки
 */
@Composable
fun BebralingoProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 16.dp,
    borderColor: Color = Color(0xFFE5E5E5)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height + 4.dp)
            .clip(RoundedCornerShape(100))
            .background(borderColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .padding(2.dp)
                .clip(RoundedCornerShape(100))
                .background(Green),
        )
    }
}
