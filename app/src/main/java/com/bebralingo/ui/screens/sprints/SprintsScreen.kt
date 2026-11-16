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
 * Р­РєСЂР°РЅ СЃРїСЂРёРЅС‚РѕРІ вЂ” РёСЃРїС‹С‚Р°РЅРёСЏ РЅР° СЃРєРѕСЂРѕСЃС‚СЊ (30СЃ, 1РјРёРЅ, 3РјРёРЅ).
 *
 * Р’ СЂРµР¶РёРјРµ РѕР¶РёРґР°РЅРёСЏ: РєР°СЂС‚РѕС‡РєРё РІС‹Р±РѕСЂР° РґР»РёС‚РµР»СЊРЅРѕСЃС‚Рё + СЂРµРєРѕСЂРґС‹ + РґРѕСЃС‚РёР¶РµРЅРёСЏ.
 * Р’ СЂРµР¶РёРјРµ Р±РµРіР°: С‚Р°Р№РјРµСЂ РѕР±СЂР°С‚РЅРѕРіРѕ РѕС‚СЃС‡С‘С‚Р°, Р¶РёРІС‹Рµ CPM, РїРѕСЃРёРјРІРѕР»СЊРЅРѕРµ РїРѕР»Рµ РІРІРѕРґР°,
 * РїРѕСЃР»Рµ РѕРєРѕРЅС‡Р°РЅРёСЏ вЂ” РґРёР°Р»РѕРіРѕРІР°СЏ РєР°СЂС‚РѕС‡РєР° СЂРµР·СѓР»СЊС‚Р°С‚РѕРІ.
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
                        text = "РЎРїСЂРёРЅС‚С‹ РЅР° СЃРєРѕСЂРѕСЃС‚СЊ",
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
                // --- Р РµР¶РёРј Р±РµРіР° / Р РµР·СѓР»СЊС‚Р°С‚С‹ ---
                RunningSprint(
                    state = state,
                    viewModel = viewModel
                )
            } else {
                // --- Р РµР¶РёРј РѕР¶РёРґР°РЅРёСЏ ---
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
            // Р’РµСЂС…РЅСЏСЏ РїР»Р°С€РєР° С‚Р°Р№РјРµСЂР° Рё CPM
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatChip(
                    emoji = "вЏ±",
                    value = "${state.remainingSeconds}СЃ",
                    label = "РћСЃС‚Р°Р»РѕСЃСЊ",
                    color = timerColor
                )
                StatChip(
                    emoji = "вљЎ",
                    value = "${state.liveCpm}",
                    label = "Р·РЅ/РјРёРЅ",
                    color = Blue
                )
            }

            Spacer(modifier = Modifier.height(Dim.gapMedium))

            // РўРµРєСѓС‰РёР№ С‚РµРєСЃС‚ Р·Р°РґР°РЅРёСЏ
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

            // РџРѕР»Рµ РІРІРѕРґР°
            if (state.isRunning) {
                BasicTextField(
                    value = state.typedText,
                    onValueChange = { viewModel.onTextChange(it) },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false
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

        // Р”РёР°Р»РѕРі Р·Р°РІРµСЂС€РµРЅРёСЏ СЃРїСЂРёРЅС‚Р°
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

        // Р—Р°РіРѕР»РѕРІРѕРє РєР°СЂС‚РѕС‡РєРё
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
                    text = "вЏ± РСЃРїС‹С‚Р°РЅРёРµ РЅР° СЃРєРѕСЂРѕСЃС‚СЊ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Blue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "РџРµС‡Р°С‚Р°Р№С‚Рµ СЃР»СѓС‡Р°Р№РЅС‹Рµ С‚РµРєСЃС‚С‹ РЅР° РІСЂРµРјСЏ Рё РїРѕР±РёРІР°Р№С‚Рµ Р»РёС‡РЅС‹Рµ СЂРµРєРѕСЂРґС‹!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(Dim.gapLarge))

        // Р’С‹Р±РѕСЂ СЂРµР¶РёРјР°
        Text(
            text = "Р’С‹Р±РµСЂРёС‚Рµ РґР»РёС‚РµР»СЊРЅРѕСЃС‚СЊ",
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

        // РљРЅРѕРїРєР° СЃС‚Р°СЂС‚Р°
        if (state.selectedMode != null) {
            SolidButton(
                text = "РќР°С‡Р°С‚СЊ СЃРїСЂРёРЅС‚ (${state.selectedMode.seconds} СЃРµРє)",
                onClick = { viewModel.startSprint() },
                color = Orange
            )
        }

        Spacer(modifier = Modifier.height(Dim.gapLarge))

        // Р РµРєРѕСЂРґ
        val bestCpm by viewModel.bestSprintCpm.collectAsState()
        if (bestCpm > 0) {
            StatChip(
                emoji = "рџЏ†",
                value = "$bestCpm Р·РЅ/РјРёРЅ",
                label = "Р›РёС‡РЅС‹Р№ СЂРµРєРѕСЂРґ CPM",
                color = Gold
            )
            Spacer(modifier = Modifier.height(Dim.gapLarge))
        }

        // Р”РѕСЃС‚РёР¶РµРЅРёСЏ
        val achievements = Achievements.ALL
        Text(
            text = "рџЏ† Р”РѕСЃС‚РёР¶РµРЅРёСЏ",
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
                    text = if (isUnlocked) ach.emoji else "рџ”’",
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
            Text(text = "СЃРµРєСѓРЅРґ", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
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
            text = "Р’СЂРµРјСЏ РІС‹С€Р»Рѕ! вЏ±",
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
                        text = "РќРѕРІРѕРµ РґРѕСЃС‚РёР¶РµРЅРёРµ: ${ach.title}",
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
                emoji = "вљЎ",
                value = "${result.cpm}",
                label = "Р·РЅ/РјРёРЅ",
                color = Blue
            )
            StatChip(
                emoji = "рџЋЇ",
                value = "${result.accuracy.toInt()}%",
                label = "РўРѕС‡РЅРѕСЃС‚СЊ",
                color = Green
            )
            StatChip(
                emoji = if (result.isPerfect) "рџ’Ћ" else "вќЊ",
                value = "${result.errorCount}",
                label = "РћС€РёР±РєРё",
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
                    text = "Р•С‰С‘ СЂР°Р·",
                    onClick = onAgain,
                    color = Orange
                )
            }
            Box(modifier = Modifier.weight(1f)) {
                SolidButton(
                    text = "Р“РѕС‚РѕРІРѕ",
                    onClick = onDone,
                    color = Green
                )
            }
        }
    }
}

