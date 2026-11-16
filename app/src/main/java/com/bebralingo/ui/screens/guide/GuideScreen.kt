package com.bebralingo.ui.screens.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bebralingo.ui.components.*
import com.bebralingo.ui.theme.*
import com.bebralingo.ui.theme.Dim

/**
 * Экран «Гайд по слепой печати» — памятка с раскладкой клавиатуры,
 * цветовой кодировкой пальцев, правилами посадки и советами.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Гайд",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dim.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dim.gapLarge))

            // Заголовок
            Text(
                text = "🖱 Слепая печать за 30 дней",
                style = MaterialTheme.typography.displayMedium,
                color = Green,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Ваш путеводитель к скорости и уверенности",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dim.gapLarge))

            // 1. Раскладка клавиатуры
            Text(
                text = "1. Раскладка и зоны пальцев",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))

            KeyboardGuide()

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            // Легенда
            Text(
                text = "Каждый цвет — свой палец:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))
            FingerLegend()

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            // 2. Домашняя позиция
            Text(
                text = "2. Домашняя позиция",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))
            HomePositionCard()

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            // 3. Правила посадки
            Text(
                text = "3. Правила посадки и работы",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))
            PostureRules()

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            // 4. Советы
            Text(
                text = "4. Как не смотреть на клавиатуру",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))
            TipsCard()

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            // 5. Как тренироваться
            Text(
                text = "5. Как тренироваться",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))
            TrainingAdviceCard()

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

/**
 * Полная раскладка клавиатуры ЙЦУКЕН с цветовой кодировкой зон.
 */
@Composable
private fun KeyboardGuide() {
    val rows = FingerMap.rows

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dim.cornerMedium))
            .background(SurfaceLight)
    ) {
        Column(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            rows.forEachIndexed { rowIdx, row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    row.forEach { key ->
                        val zone = if (key.length == 1) FingerMap.getZone(key[0]) else FingerZone.None

                        val isHome = key.length == 1 && key in FingerMap.homeRowKeys

                        GuideKey(
                            letter = key,
                            zone = zone,
                            isHomeRowKey = isHome,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
                if (rowIdx < rows.size - 1) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

/**
 * Цветовая легенда для пальцев.
 */
@Composable
private fun FingerLegend() {
    val legend = listOf(
        FingerZone.LeftPinky to "Левый мизинец",
        FingerZone.LeftRing to "Левый безымянный",
        FingerZone.LeftMiddle to "Левый средний",
        FingerZone.LeftIndex to "Левый указательный",
        FingerZone.RightIndex to "Правый указательный",
        FingerZone.RightMiddle to "Правый средний",
        FingerZone.RightRing to "Правый безымянный",
        FingerZone.RightPinky to "Правый мизинец",
        FingerZone.Thumb to "Большие пальцы (пробел)",
        FingerZone.None to "Модификаторы (Shift, Tab...)"
    )

    legend.forEach { (zone, label) ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(zone.color.copy(0.3f))
                    .border(1.5.dp, zone.color, RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}

/**
 * Карточка домашней позиции с визуализацией клавиш А и О.
 */
@Composable
private fun HomePositionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dim.cornerMedium),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF0))
    ) {
        Column(modifier = Modifier.padding(Dim.paddingMedium)) {
            Text(
                text = "🏠 Домашняя позиция",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Green
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))

            Text(
                text = "Левая рука resting на: Ф Ы В А",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Правая рука resting на: О Л Д Ж",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))

            // Визуализация домашней позиции
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Левая рука
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Левая", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Row {
                        GuideKey("ф", FingerZone.LeftPinky)
                        GuideKey("ы", FingerZone.LeftRing)
                        GuideKey("в", FingerZone.LeftMiddle)
                        GuideKey("а", FingerZone.LeftIndex)
                    }
                }
                // Правая рука
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Правая", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                    Row {
                        GuideKey("о", FingerZone.RightIndex)
                        GuideKey("л", FingerZone.RightMiddle)
                        GuideKey("д", FingerZone.RightRing)
                        GuideKey("ж", FingerZone.RightPinky)
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dim.gapSmall))

            Text(
                text = "🔍 Клавиши А и О имеют насечки — нащупайте их пальцами!",
                style = MaterialTheme.typography.bodyMedium,
                color = Orange,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Правила посадки за клавиатурой.
 */
@Composable
private fun PostureRules() {
    val rules = listOf(
        "🪑 Сидите прямо, ступни на полу, экран на уровне глаз" to "Правильная осанка — залог быстрой печати без боли в спине",
        "👀 Не смотрите на клавиатуру! Смотрите на экран" to "Заклейте клавиатуру прозрачной плёнкой, чтобы не соблазниться",
        "🖐 Пальцы согнуты, как будто держите апельсин" to "Нажимайте клавиши кончиками пальцев, а не плоскими пальцами",
        "🔄 Ошибки — это нормально. Скорость придёт с практикой" to "Не торопитесь: точность важнее скорости на первых этапах"
    )

    rules.forEach { (rule, hint) ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dim.cornerSmall),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(Dim.paddingMedium)) {
                Text(
                    text = rule,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

/**
 * Советы, как перестать смотреть на клавиатуру.
 */
@Composable
private fun TipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dim.cornerMedium),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
    ) {
        Column(modifier = Modifier.padding(Dim.paddingMedium)) {
            Text(
                text = "💡 5 способов не смотреть на клавиатуру",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Orange
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))

            val tips = listOf(
                "Положите руки на домашнюю позицию и закройте глаза на 5 секунд — запомните положение",
                "Наклейте стикер с домашним рядом (ФЫВА ОЛДЖ) прямо на клавиатуру",
                "Тренируйтесь печатать заметки в блокноте — без Ctrl+Z, без возврата",
                "Используйте антистресс или мячик в руке, чтобы расслабить пальцы между подходами",
                "Заклейте клавиатуру прозрачным скотчем — так вы не увидите букв и не захотите смотреть"
            )

            tips.forEachIndexed { idx, tip ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Text(
                        text = "${idx + 1}. ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Green
                    )
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

/**
 * Советы по тренировкам.
 */
@Composable
private fun TrainingAdviceCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dim.cornerMedium),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
    ) {
        Column(modifier = Modifier.padding(Dim.paddingMedium)) {
            Text(
                text = "📚 Как тренироваться",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Green
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))

            val advice = listOf(
                "Начните с блока 1 (ФЫВА ОЛДЖ) и проходите уроки по порядку",
                "Не переходите к следующему уроку, пока не наберёте 80% точности",
                "Тренируйтесь по 15–20 минут в день — лучше чаще, но меньше",
                "Используйте спринты для развития скорости после освоения клавиш",
                "Повторяйте пройденные уроки — мышечная память укрепляется повторением",
                "Следите за серией 🔥 — ежедневная практика даёт лучший результат"
            )

            advice.forEachIndexed { idx, tip ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Green,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
