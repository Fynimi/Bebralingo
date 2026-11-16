package com.bebralingo.ui.screens.sprints

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bebralingo.model.*
import com.bebralingo.ui.components.*
import com.bebralingo.ui.theme.*
import com.bebralingo.ui.theme.Dim
import kotlinx.coroutines.delay

/**
 * Экран спринтов — испытания на скорость (30с, 1мин, 3мин).
 *
 * В режиме ожидания: карточки выбора длительности + рекорды + достижения.
 * В режиме бега: таймер обратного отсчёта, живые CPM, посимвольное поле ввода,
 * после окончания — диалоговая карточка результатов.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SprintsScreen(
    viewModel: SprintsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Спринты на скорость",
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
                .padding(horizontal = Dim.paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isRunning || state.finished) {
                // --- Режим бега / Результаты ---
                RunningSprint(
                    state = state,
                    viewModel = viewModel
                )
            } else {
                // --- Режим ожидания ---
                WaitingSprint(
                    state = state,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun RunningSprint(
    state: SprintsState,
    viewModel: SprintsViewModel
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.isRunning) {
        if (state.isRunning) {
            delay(100)
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    LaunchedEffect(state.finished) {
        if (state.finished) {
            keyboardController?.hide()
        }
    }

    val timerColor = when {
        state.remainingSeconds > 10 -> Green
        state.remainingSeconds > 5 -> Orange
        else -> Red
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Верхняя плашка таймера и CPM
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatChip(
                    emoji = "⏱",
                    value = "${state.remainingSeconds}с",
                    label = "Осталось",
                    color = timerColor
                )
                StatChip(
                    emoji = "⚡",
                    value = "${state.liveCpm}",
                    label = "зн/мин",
                    color = Blue
                )
            }

            Spacer(modifier = Modifier.height(Dim.gapMedium))

            // Текущий текст задания
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(Dim.cornerMedium),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                TypingTextDisplay(
                    target = state.currentText,
                    typed = state.typedText,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            // Поле ввода
            if (state.isRunning) {
                BasicTextField(
                    value = state.typedText,
                    onValueChange = { viewModel.onTextChange(it) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = false
                    ),
                    textStyle = TextStyle(
                        color = Color.Transparent,
                        fontSize = 1.sp
                    ),
                    cursorBrush = SolidColor(Color.Transparent),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .height(1.dp)
                )
            }
        }

        // Диалог завершения спринта
        AnimatedVisibility(visible = state.finished && state.result != null) {
            state.result?.let { result ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.65f)),
                    contentAlignment = Alignment.Center
                ) {
                    ConfettiBurst(visible = true, modifier = Modifier.fillMaxSize())

                    SprintResultCard(
                        result = result,
                        unlockedAchievements = state.unlockedAchievements,
                        onAgain = { viewModel.startSprint() },
                        onDone = { viewModel.reset() },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(Dim.paddingMedium)
                    )
                }
            }
        }
    }
}

@Composable
private fun WaitingSprint(
    state: SprintsState,
    viewModel: SprintsViewModel
) {
    val progress by viewModel.userProgress.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = Dim.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(Dim.gapMedium))

        // Заголовок карточки
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dim.cornerMedium),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(Dim.paddingMedium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⏱ Испытание на скорость",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Blue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Печатайте случайные тексты на время и побивайте личные рекорды!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(Dim.gapLarge))

        // Выбор режима
        Text(
            text = "Выберите длительность",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(Dim.gapMedium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            viewModel.sprintModes.forEach { mode ->
                SprintModeCard(
                    mode = mode,
                    selected = state.selectedMode?.id == mode.id,
                    onClick = { viewModel.selectMode(mode) }
                )
            }
        }

        Spacer(modifier = Modifier.height(Dim.gapLarge))

        // Кнопка старта
        if (state.selectedMode != null) {
            SolidButton(
                text = "Начать спринт (${state.selectedMode.seconds} сек)",
                onClick = { viewModel.startSprint() },
                color = Orange
            )
        }

        Spacer(modifier = Modifier.height(Dim.gapLarge))

        // Рекорд
        val bestCpm by viewModel.bestSprintCpm.collectAsState()
        if (bestCpm > 0) {
            StatChip(
                emoji = "🏆",
                value = "$bestCpm зн/мин",
                label = "Личный рекорд CPM",
                color = Gold
            )
            Spacer(modifier = Modifier.height(Dim.gapLarge))
        }

        // Достижения
        val achievements = Achievements.ALL
        Text(
            text = "🏆 Достижения",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(Dim.gapSmall))

        achievements.forEach { ach ->
            val isUnlocked = ach.id in progress.unlockedAchievements
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(Dim.cornerSmall))
                    .background(
                        if (isUnlocked) Green.copy(alpha = 0.10f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isUnlocked) ach.emoji else "🔒",
                    fontSize = 24.sp,
                    modifier = Modifier.alpha(if (isUnlocked) 1f else 0.5f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = ach.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) TextPrimary else TextLight
                    )
                    Text(
                        text = ach.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun SprintModeCard(
    mode: SprintMode,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(100.dp)
            .height(100.dp)
            .clip(RoundedCornerShape(Dim.cornerMedium))
            .background(if (selected) Orange.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surfaceVariant)
            .border(2.dp, if (selected) Orange else Color.Transparent, RoundedCornerShape(Dim.cornerMedium))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = mode.seconds.toString(), style = MaterialTheme.typography.displayMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
            Text(text = "секунд", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        }
    }
}

@Composable
private fun SprintResultCard(
    result: TrainingResult,
    unlockedAchievements: List<Achievement>,
    onAgain: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Dim.cornerMedium))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Dim.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Время вышло! ⏱",
            style = MaterialTheme.typography.displayMedium,
            color = Orange,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(Dim.gapMedium))

        XpBadge(xp = result.xpGained, color = Gold)

        if (unlockedAchievements.isNotEmpty()) {
            Spacer(modifier = Modifier.height(Dim.gapMedium))
            unlockedAchievements.forEach { ach ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = ach.emoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Новое достижение: ${ach.title}",
                        style = MaterialTheme.typography.bodySmall,
                        color = GreenDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dim.gapMedium))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatChip(
                emoji = "⚡",
                value = "${result.cpm}",
                label = "зн/мин",
                color = Blue
            )
            StatChip(
                emoji = "🎯",
                value = "${result.accuracy.toInt()}%",
                label = "Точность",
                color = Green
            )
            StatChip(
                emoji = if (result.isPerfect) "💎" else "❌",
                value = "${result.errorCount}",
                label = "Ошибки",
                color = if (result.isPerfect) Purple else Red
            )
        }

        Spacer(modifier = Modifier.height(Dim.gapLarge))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dim.gapMedium)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                SolidButton(
                    text = "Ещё раз",
                    onClick = onAgain,
                    color = Orange
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                SolidButton(
                    text = "Готово",
                    onClick = onDone,
                    color = Green
                )
            }
        }
    }
}

