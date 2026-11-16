package com.bebralingo.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.bebralingo.model.*
import com.bebralingo.ui.components.*
import com.bebralingo.ui.theme.*
import com.bebralingo.ui.theme.Dim

/**
 * Главный экран — карта пути обучения.
 * Вертикальный список блоков с узлами-уровнями в стиле Duolingo.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonMapScreen(
    viewModel: MapViewModel,
    onStartLesson: (LessonId) -> Unit,
    onOpenGuide: () -> Unit,
    onOpenSprints: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by viewModel.progress.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Путь обучения",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Кнопка гида
                    IconButton(onClick = onOpenGuide) {
                        Icon(Icons.Default.MenuBook, contentDescription = "Гайд", tint = Green)
                    }
                    // Кнопка спринтов
                    IconButton(onClick = onOpenSprints) {
                        Icon(Icons.Default.Timer, contentDescription = "Спринты", tint = Blue)
                    }
                    Spacer(Modifier.width(8.dp))
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
            // Верхняя панель статистики
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatChip(
                    emoji = "🔥",
                    value = "${progress.streak} дн.",
                    label = "Ударный режим",
                    color = Orange
                )
                StatChip(
                    emoji = "⚡",
                    value = "${progress.xp}",
                    label = "XP",
                    color = Gold
                )
                StatChip(
                    emoji = "🏆",
                    value = "${progress.bestSprintCpm}",
                    label = "Рекорд CPM",
                    color = Blue
                )
            }

            Spacer(modifier = Modifier.height(Dim.gapMedium))

            // Блоки уроков
            val blocks = viewModel.blocks
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(blocks) { block ->
                    LessonBlockCard(
                        block = block,
                        completedCount = viewModel.completedInBlock(block.id),
                        totalCount = viewModel.totalInBlock(block.id),
                        viewModel = viewModel,
                        onStartLesson = onStartLesson,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun LessonBlockCard(
    block: LessonBlock,
    completedCount: Int,
    totalCount: Int,
    viewModel: MapViewModel,
    onStartLesson: (LessonId) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Баннер блока
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp)
                .clip(RoundedCornerShape(Dim.cornerMedium))
                .background(block.accent)
                .padding(horizontal = Dim.paddingMedium, vertical = Dim.paddingSmall),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                Text(
                    text = block.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = block.description,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            // Прогресс в блоке
            val pct = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(70.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.35f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(pct)
                        .fillMaxHeight()
                        .background(Color.White)
                        .clip(RoundedCornerShape(3.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(Dim.gapMedium))

        // Уроки внутри блока
        block.lessons.forEachIndexed { idx, lesson ->
            val status = viewModel.getLessonStatus(lesson.id)

            LessonNode(
                lesson = lesson,
                status = status,
                zigzagOffset = zigzagOffset(idx),
                onClick = {
                    if (status != LessonStatus.LOCKED) {
                        onStartLesson(lesson.id)
                    }
                }
            )
        }
    }
}

/**
 * Зигзагообразное смещение урока на карте (вправо/влево).
 */
private fun zigzagOffset(index: Int): Float {
    return when (index % 4) {
        0 -> -60f
        1 -> 60f
        2 -> -30f
        else -> 30f
    }
}

@Composable
private fun LessonNode(
    lesson: Lesson,
    status: LessonStatus,
    zigzagOffset: Float,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .offset(x = zigzagOffset.dp)
                .padding(vertical = 4.dp)
                .clickable(
                    enabled = status != LessonStatus.LOCKED,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (status) {
                    LessonStatus.COMPLETED -> {
                        NodeButton(
                            color = Green
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    LessonStatus.CURRENT -> {
                        Box(
                            modifier = Modifier
                                .size(Dim.nodeSize + 10.dp)
                                .clip(CircleShape)
                                .background(Orange.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            NodeButton(
                                color = Orange
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                    LessonStatus.LOCKED -> {
                        NodeButton(
                            color = Color(0xFFD0D0D0)
                        ) {
                            Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (status == LessonStatus.LOCKED) TextLight else TextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = lesson.subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

