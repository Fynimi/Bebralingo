package com.bebralingo.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bebralingo.ui.theme.Gold
import com.bebralingo.ui.theme.Orange
import com.bebralingo.ui.theme.Dim

/**
 * Карточка-статистика: эмодзи + значение + подпись.
 * Используется в шапке карты и на экранах результатов.
 */
@Composable
fun StatChip(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    color: Color = Orange
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Dim.cornerSmall))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * Бейдж начисленного XP с анимацией счёта.
 */
@Composable
fun XpBadge(
    xp: Int,
    modifier: Modifier = Modifier,
    color: Color = Gold
) {
    val animatedXp by animateIntAsState(
        targetValue = xp,
        animationSpec = androidx.compose.animation.core.tween(900),
        label = "xp_count"
    )

    Text(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 14.dp, vertical = 6.dp),
        text = "+$animatedXp XP",
        style = MaterialTheme.typography.labelLarge,
        color = color
    )
}
