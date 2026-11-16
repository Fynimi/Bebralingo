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
 * Р­РєСЂР°РЅ С‚СЂРµРЅРёСЂРѕРІРєРё вЂ” РѕСЃРЅРѕРІРЅРѕР№ РіРµР№РјРїР»РµР№.
 *
 * РћС‚РѕР±СЂР°Р¶Р°РµС‚ С†РµР»РµРІРѕР№ С‚РµРєСЃС‚ СЃ РїРѕСЃРёРјРІРѕР»СЊРЅРѕР№ РїРѕРґСЃРІРµС‚РєРѕР№,
 * РїСЂРёРЅРёРјР°РµС‚ РІРІРѕРґ С‡РµСЂРµР· СЃРєСЂС‹С‚РѕРµ РїРѕР»Рµ BasicTextField,
 * РїРѕРґРґРµСЂР¶РёРІР°РµС‚ Р°РїРїР°СЂР°С‚РЅС‹Рµ (OTG/Bluetooth) Рё СЃРёСЃС‚РµРјРЅС‹Рµ СЌРєСЂР°РЅРЅС‹Рµ РєР»Р°РІРёР°С‚СѓСЂС‹,
 * РїРѕРєР°Р·С‹РІР°РµС‚ CPM Рё С‚РѕС‡РЅРѕСЃС‚СЊ РІ СЂРµР°Р»СЊРЅРѕРј РІСЂРµРјРµРЅРё,
 * Р° РїСЂРё Р·Р°РІРµСЂС€РµРЅРёРё вЂ” СЌС„С„РµРєС‚РЅС‹Р№ РѕРІРµСЂР»РµР№ СЃ РєРѕРЅС„РµС‚С‚Рё.
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

    LaunchedEffect(state.finished) {
        if (state.finished) {
            keyboardController?.hide()
        }
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
                            contentDescription = "Р—Р°РєСЂС‹С‚СЊ",
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
                            emoji = "вљЎ",
                            value = "${state.liveStats.cpm}",
                            label = "Р·РЅ/РјРёРЅ",
                            color = Blue
                        )
                        StatChip(
                            emoji = "рџЋЇ",
                            value = "${state.liveStats.accuracy.toInt()}%",
                            label = "РўРѕС‡РЅРѕСЃС‚СЊ",
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
                    .verticalScroll(rememberScrollState())
                    .padding(top = 16.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // РљР°СЂС‚РѕС‡РєР° СЃ РЅР°Р·РІР°РЅРёРµРј Рё РїРѕРґСЃРєР°Р·РєРѕР№
                if (state.lessonTitle.isNotEmpty()) {
                    Text(
                        text = state.lessonTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                // Р РµРЅРґРµСЂРёРЅРі С†РµР»РµРІРѕРіРѕ С‚РµРєСЃС‚Р° РїРѕ СЃРёРјРІРѕР»Р°Рј
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
                        target = state.target,
                        typed = state.typed,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    )
                }

                // РџРѕР»Рµ РІРІРѕРґР° (СЂР°Р±РѕС‚Р°РµС‚ СЃ СЌРєСЂР°РЅРЅРѕР№ Рё С„РёР·РёС‡РµСЃРєРѕР№ OTG/Bluetooth РєР»Р°РІРёР°С‚СѓСЂРѕР№)
                if (!state.finished) {
                    BasicTextField(
                        value = state.typed,
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

                if (!state.started && !state.finished) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "РќР°С‡РЅРёС‚Рµ РїРµС‡Р°С‚СЊ РЅР° РєР»Р°РІРёР°С‚СѓСЂРµ вЊЁпёЏ",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = Green
                    )
                }
            }

            // РћРІРµСЂР»РµР№ Р·Р°РІРµСЂС€РµРЅРёСЏ
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
                text = "РЈСЂРѕРє РїСЂРѕР№РґРµРЅ! рџЋ‰",
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
                    value = if (result.isPerfect) "0" else result.errorCount.toString(),
                    label = "РћС€РёР±РєРё",
                    color = if (result.isPerfect) Purple else Red
                )
            }

            Spacer(modifier = Modifier.height(Dim.gapLarge))

            SolidButton(
                text = "РџСЂРѕРґРѕР»Р¶РёС‚СЊ",
                onClick = onContinue,
                color = Green
            )
        }
    }
}

