package com.bebralingo.ui.screens.guide

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bebralingo.ui.components.GuideKey
import com.bebralingo.ui.components.MobileKeyboard
import com.bebralingo.ui.components.ThumbZone
import com.bebralingo.ui.theme.*
import com.bebralingo.ui.theme.Dim

/**
 * Экран «Гайд» — памятка по быстрой печати на телефоне.
 *
 * Материал ориентирован на сенсорную клавиатуру: зоны больших пальцев,
 * хват телефона, настройки клавиатуры и режим тренировок.
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

            Text(
                text = "📱 Печать двумя большими пальцами",
                style = MaterialTheme.typography.displayMedium,
                color = Blue,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Как быстро и уверенно печатать на экране телефона",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(Dim.gapLarge))

            SectionTitle("1. Клавиатура и зоны пальцев")
            PhoneKeyboardGuide()

            Spacer(modifier = Modifier.height(Dim.gapSmall))
            Text(
                text = "Цвет показывает, каким большим пальцем удобнее нажимать клавишу:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))
            ThumbLegend()

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            SectionTitle("2. Как держать телефон")
            GripCard()

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            SectionTitle("3. Посадка и осанка")
            PostureRules()

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            SectionTitle("4. Настройки клавиатуры")
            SettingsCard()

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            SectionTitle("5. Как не смотреть на клавиши")
            TipsCard()

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            SectionTitle("6. Как тренироваться")
            TrainingAdviceCard()

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = Dim.gapSmall)
    )
}

/**
 * Схема экранной клавиатуры: три буквенных ряда и служебный нижний ряд.
 * Клавиши окрашены по зоне большого пальца.
 */
@Composable
private fun PhoneKeyboardGuide() {
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
            MobileKeyboard.letterRows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    row.forEachIndexed { index, char ->
                        GuideKey(
                            letter = char.toString(),
                            zone = MobileKeyboard.zoneOf(index, row.length),
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                MobileKeyboard.bottomRow.forEach { key ->
                    GuideKey(
                        letter = key,
                        zone = MobileKeyboard.bottomZone(key),
                        minWidth = if (key == "пробел") 120.dp else 56.dp,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Легенда зон больших пальцев.
 */
@Composable
private fun ThumbLegend() {
    val legend = listOf(
        ThumbZone.Left to "Левый большой палец — левая половина",
        ThumbZone.Right to "Правый большой палец — правая половина",
        ThumbZone.Both to "Пробел — удобным пальцем"
    )

    legend.forEach { (zone, label) ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(zone.color.copy(alpha = 0.3f))
                    .border(1.5.dp, zone.color, RoundedCornerShape(6.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Карточка правильного хвата телефона.
 */
@Composable
private fun GripCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dim.cornerMedium),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F4FF))
    ) {
        Column(modifier = Modifier.padding(Dim.paddingMedium)) {
            Text(
                text = "🤲 Две руки, два больших пальца",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Blue
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))

            val rules = listOf(
                "Держите телефон двумя руками, а не одной",
                "Большие пальцы — над нижним рядом клавиш",
                "Каждый палец работает на своей половине экрана",
                "Пробел нажимайте удобным пальцем, не перекладывая телефон",
                "В горизонтальной ориентации печатать удобнее и быстрее"
            )

            rules.forEach { rule ->
                Text(
                    text = "• $rule",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dim.gapMedium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GuideKey(
                    letter = "левый",
                    zone = ThumbZone.Left,
                    minWidth = 88.dp,
                    fontSize = 13.sp
                )
                GuideKey(
                    letter = "пробел",
                    zone = ThumbZone.Both,
                    minWidth = 88.dp,
                    fontSize = 13.sp
                )
                GuideKey(
                    letter = "правый",
                    zone = ThumbZone.Right,
                    minWidth = 88.dp,
                    fontSize = 13.sp
                )
            }
        }
    }
}

/**
 * Правила посадки при печати на телефоне.
 */
@Composable
private fun PostureRules() {
    val rules = listOf(
        "📏 Экран в 25–30 см от глаз" to "Держите телефон на уровне груди и не наклоняйте голову вниз",
        "🪑 Спина прямая, плечи расслаблены" to "Локти прижаты к телу — так большие пальцы меньше устают",
        "🚶 Не печатайте на ходу и лёжа" to "В тряске и неудобной позе промахов заметно больше",
        "⏱ Пауза каждые 15–20 минут" to "Разомните кисти и большие пальцы, посмотрите вдаль"
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

/**
 * Настройки клавиатуры, которые мешают научиться печатать.
 */
@Composable
private fun SettingsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dim.cornerMedium),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
    ) {
        Column(modifier = Modifier.padding(Dim.paddingMedium)) {
            Text(
                text = "⚙️ Что выключить в клавиатуре",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Orange
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))

            val settings = listOf(
                "Автозамена и подсказки — они исправляют ошибки за вас, и вы их не замечаете",
                "Свайп-ввод — тренируем нажатия, а не свайпы",
                "Автозаглавные буквы и автопробел — пусть их ставите вы",
                "Сложные темы и анимации — простая раскладка читается быстрее"
            )

            settings.forEachIndexed { index, item ->
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text(
                        text = "${index + 1}. ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Orange
                    )
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

/**
 * Советы, как перестать смотреть на клавиши.
 */
@Composable
private fun TipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dim.cornerMedium),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FFF0))
    ) {
        Column(modifier = Modifier.padding(Dim.paddingMedium)) {
            Text(
                text = "👀 5 способов не смотреть на клавиши",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Green
            )
            Spacer(modifier = Modifier.height(Dim.gapSmall))

            val tips = listOf(
                "Смотрите на строку с текстом задания, а не на клавиатуру под ней",
                "Печатайте короткие слова целиком, не проверяя каждую букву",
                "Ошибку исправляйте, не глядя на клавиши — ориентируйтесь по памяти",
                "Держите телефон устойчиво: от дрожания рук появляются лишние промахи",
                "Переписывайте свои заметки в тренажёре, а не в мессенджере"
            )

            tips.forEachIndexed { index, tip ->
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
                    Text(
                        text = "${index + 1}. ",
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
 * Советы по тренировкам на телефоне.
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
                "Начните с блока «Основной ряд» и проходите уроки по порядку",
                "Печатайте двумя большими пальцами с первого урока",
                "Сначала точность: без ошибок, и только потом быстрее",
                "15 минут в день лучше, чем час раз в неделю",
                "Повторяйте пройденные уроки — пальцы запоминают движения",
                "Следите за серией 🔥 — ежедневная практика даёт лучший результат"
            )

            advice.forEach { tip ->
                Row(modifier = Modifier.padding(vertical = 3.dp)) {
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
