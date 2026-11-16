package com.bebralingo.ui.screens.training

import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.delay

/**
 * Экран тренировки — основной геймплей.
 *
 * Отображает целевой текст с посимвольной подсветкой,
 * принимает ввод через скрытое поле BasicTextField,
 * поддерживает аппаратные (OTG/Bluetooth) и системные экранные клавиатуры,
 * показывает CPM и точность в реальном времени,
 * а при завершении — эффектный оверлей с конфетти.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingScreen(
    viewModel: TrainingViewModel,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val shakeAnim = remember { Animatable(0f) }

    LaunchedEffect(state.shakeTick) {
        if (state.shakeTick > 0) {
            val vibrator = context.getSystemService(Vibrator::class.java)
            if (vibrator?.hasVibrator() == true) {
                vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            }
            shakeAnim.animateTo(-12f, tween(40))
            shakeAnim.animateTo(12f, tween(60))
            shakeAnim.animateTo(-8f, tween(60))
            shakeAnim.animateTo(0f, tween(60))
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dim.paddingMedium, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        keyboardController?.hide()
                        onClose()
                    }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Закрыть",
                            tint = TextSecondary
                        )
                    }
                    val progress = if (state.target.isNotEmpty())
                        state.typed.length.toFloat() / state.target.length
                    else 0f
                    Box(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                        BebralingoProgressBar(progress = progress)
                    }
                }
                if (state.started && !state.finished) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dim.paddingMedium, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatChip(
                            emoji = "⚡",
                            value = "${state.liveStats.cpm}",
                            label = "зн/мин",
                            color = Blue
                        )
                        StatChip(
                            emoji = "🎯",
                            value = "${state.liveStats.accuracy.toInt()}%",
                            label = "Точность",
                            color = Green
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dim.paddingMedium)
                .offset(x = shakeAnim.value.dp)
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
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Карточка с названием и подсказкой
                if (state.lessonTitle.isNotEmpty()) {
                    Text(
                        text = state.lessonTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Рендеринг целевого текста по символам
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(Dim.cornerMedium),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    TargetTextDisplay(
                        target = state.target,
                        typed = state.typed,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                }

                // Поле ввода (работает с экранной и физической OTG/Bluetooth клавиатурой)
                if (!state.finished) {
                    BasicTextField(
                        value = state.typed,
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

                if (!state.started && !state.finished) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Начните печать на клавиатуре ⌨️",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Green
                    )
                }
            }

            // Оверлей завершения
            AnimatedVisibility(visible = state.finished && state.result != null) {
                state.result?.let { res ->
                    CompletionOverlay(
                        result = res,
                        lessonTitle = state.lessonTitle,
                        onContinue = onClose,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

/**
 * Отображает целевой текст по символам:
 * - Зелёный фон — правильно набранный
 * - Красный фон — ошибка
 * - Оранжевая рамка/курсор — текущий символ
 * - Серый — ожидающий
 */
@Composable
private fun TargetTextDisplay(
    target: String,
    typed: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val words = target.split(" ")
        var currentGlobalIdx = 0

        val lineCharLimit = 22
        var currentLineChars = 0
        var currentLineWords = mutableListOf<String>()
        val linesOfWords = mutableListOf<List<String>>()

        for (word in words) {
            val len = word.length + 1
            if (currentLineChars + len > lineCharLimit && currentLineWords.isNotEmpty()) {
                linesOfWords.add(currentLineWords)
                currentLineWords = mutableListOf(word)
                currentLineChars = len
            } else {
                currentLineWords.add(word)
                currentLineChars += len
            }
        }
        if (currentLineWords.isNotEmpty()) {
            linesOfWords.add(currentLineWords)
        }

        var charTracker = 0
        linesOfWords.forEachIndexed { lineIdx, lineWords ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                lineWords.forEachIndexed { wordIdx, word ->
                    word.forEach { char ->
                        val globalIdx = charTracker
                        TargetChar(
                            char = char,
                            isTyped = globalIdx < typed.length,
                            isCorrect = globalIdx < typed.length && typed[globalIdx] == char,
                            isCurrent = globalIdx == typed.length
                        )
                        charTracker++
                    }

                    // Пробел между словами (если не последнее слово в последней строке)
                    if (charTracker < target.length && target[charTracker] == ' ') {
                        val globalIdx = charTracker
                        TargetChar(
                            char = ' ',
                            isTyped = globalIdx < typed.length,
                            isCorrect = globalIdx < typed.length && typed[globalIdx] == ' ',
                            isCurrent = globalIdx == typed.length
                        )
                        charTracker++
                    }
                }
            }
        }
    }
}

@Composable
private fun TargetChar(
    char: Char,
    isTyped: Boolean,
    isCorrect: Boolean,
    isCurrent: Boolean
) {
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
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (char == ' ') "␣" else char.toString(),
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun CompletionOverlay(
    result: TrainingResult,
    lessonTitle: String = "",
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.65f))
    ) {
        ConfettiBurst(visible = true, modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.9f)
                .padding(horizontal = Dim.paddingMedium)
                .clip(RoundedCornerShape(Dim.cornerMedium))
                .background(MaterialTheme.colorScheme.surface)
                .padding(Dim.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Урок пройден! 🎉",
                style = MaterialTheme.typography.displayMedium,
                color = Green,
                fontWeight = FontWeight.Bold
            )
            if (lessonTitle.isNotEmpty()) {
                Text(
                    text = lessonTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(Dim.gapMedium))

            XpBadge(xp = result.xpGained, color = Gold)
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
                    value = if (result.isPerfect) "0" else result.errorCount.toString(),
                    label = "Ошибки",
                    color = if (result.isPerfect) Purple else Red
                )
            }

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            SolidButton(
                text = "Продолжить",
                onClick = onContinue,
                color = Green
            )
        }
    }
}

