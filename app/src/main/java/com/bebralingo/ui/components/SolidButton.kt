package com.bebralingo.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bebralingo.ui.theme.*
import com.bebralingo.ui.theme.Dim

/**
 * Объёмная (3D) кнопка с нижней тенью, как в Duolingo.
 * При нажатии «утапливается» — смещается вниз на величину тени.
 *
 * @param color акцентный цвет кнопки
 * @param onClick обработчик нажатия
 * @param modifier модификатор
 * @param enabled состояние доступности
 * @param contentInside текст/контент внутри кнопки
 */
@Composable
fun SolidButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Green,
    enabled: Boolean = true,
    contentInside: (@Composable RowScope.() -> Unit)? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val shadowOffset by animateDpAsState(
        targetValue = if (isPressed) Dim.shadowOffsetCompact else Dim.shadowOffset,
        label = "shadow"
    )

    Box {
        // Тень-подложка (нижний слой)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dim.buttonHeight)
                .offset(y = shadowOffset)
                .clip(RoundedCornerShape(Dim.cornerMedium))
                .background(color.copy(alpha = 0.5f))
        )

        // Основная кнопка
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(Dim.buttonHeight)
                .clip(RoundedCornerShape(Dim.cornerMedium))
                .background(if (enabled) color else Color.Gray.copy(alpha = 0.3f))
                .clickable(
                    enabled = enabled,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (enabled) {
                        isPressed = true
                        onClick()
                        isPressed = false
                    }
                }
                .padding(horizontal = Dim.paddingMedium, vertical = Dim.paddingSmall),
            contentAlignment = Alignment.Center
        ) {
            if (contentInside != null) {
                Row(content = contentInside)
            } else {
                Text(
                    text = text.uppercase(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Версия кнопки для узла на карте (круглая, компактная). */
@Composable
fun NodeButton(
    modifier: Modifier = Modifier,
    color: Color = Green,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(Dim.nodeSize)
            .clip(RoundedCornerShape(Dim.cornerCircle))
            .background(color)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

