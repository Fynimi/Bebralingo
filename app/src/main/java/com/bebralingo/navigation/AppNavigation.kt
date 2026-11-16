package com.bebralingo.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bebralingo.data.AppDataStore
import com.bebralingo.data.LessonContent
import com.bebralingo.ui.screens.guide.GuideScreen
import com.bebralingo.ui.screens.map.LessonMapScreen
import com.bebralingo.ui.screens.map.MapViewModel
import com.bebralingo.ui.screens.sprints.SprintsScreen
import com.bebralingo.ui.screens.sprints.SprintsViewModel
import com.bebralingo.ui.screens.training.TrainingScreen
import com.bebralingo.ui.screens.training.TrainingViewModel
import com.bebralingo.ui.theme.*

/**
 * Маршруты навигации.
 */
object Routes {
    const val MAP = "map"
    const val SPRINTS = "sprints"
    const val GUIDE = "guide"
    const val TRAINING = "training"

    fun training(lessonId: String) = "$TRAINING/$lessonId"
}

/**
 * Элементы нижней навигации.
 */
private data class BottomItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomItems = listOf(
    BottomItem(Routes.MAP, "Путь", Icons.Default.Map),
    BottomItem(Routes.SPRINTS, "Спринты", Icons.Default.Timer),
    BottomItem(Routes.GUIDE, "Гайд", Icons.Default.MenuBook)
)

/**
 * Простая фабрика ViewModel: инжектит AppDataStore в конструктор.
 */
private class VMFactory(
    private val store: AppDataStore,
    private val lessonId: String = ""
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MapViewModel::class.java) ->
                MapViewModel(store) as T

            modelClass.isAssignableFrom(SprintsViewModel::class.java) ->
                SprintsViewModel(store) as T

            modelClass.isAssignableFrom(TrainingViewModel::class.java) ->
                TrainingViewModel(store, lessonId) as T

            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}

/**
 * Главный навигатор приложения.
 * 3 основных экрана через нижнюю панель + фуллскрин тренировки.
 */
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val dataStore = remember { AppDataStore(context) }

    val currentRoute = navController.currentBackStackEntryAsState()
        .value?.destination?.route

    // На экране тренировки нижняя панель скрыта
    val isTraining = currentRoute?.startsWith(Routes.TRAINING) == true

    Scaffold(
        bottomBar = {
            if (!isTraining) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(Routes.MAP)
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.MAP,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.MAP) {
                val vm: MapViewModel = viewModel(
                    factory = VMFactory(dataStore)
                )
                LessonMapScreen(
                    viewModel = vm,
                    onStartLesson = { lessonId ->
                        navController.navigate(Routes.training(lessonId))
                    },
                    onOpenGuide = {
                        navController.navigate(Routes.GUIDE)
                    },
                    onOpenSprints = {
                        navController.navigate(Routes.SPRINTS)
                    }
                )
            }
            composable(Routes.SPRINTS) {
                val vm: SprintsViewModel = viewModel(
                    factory = VMFactory(dataStore)
                )
                SprintsScreen(viewModel = vm)
            }
            composable(Routes.GUIDE) {
                GuideScreen()
            }
            trainingGraph(navController, dataStore)
        }
    }
}

private fun NavGraphBuilder.trainingGraph(
    navController: NavHostController,
    dataStore: AppDataStore
) {
    composable(
        "${Routes.TRAINING}/{lessonId}",
        arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
    ) { backStackEntry ->
        val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
        val vm: TrainingViewModel = viewModel(
            factory = VMFactory(dataStore, lessonId)
        )
        TrainingScreen(
            viewModel = vm,
            onClose = { navController.popBackStack() },
            onContinue = {
                val lessons = LessonContent.allLessons
                val currentIndex = lessons.indexOfFirst { it.id == lessonId }
                val next = lessons.getOrNull(currentIndex + 1)
                if (next != null) {
                    navController.navigate(Routes.training(next.id)) {
                        popUpTo(Routes.MAP)
                    }
                } else {
                    navController.popBackStack()
                }
            }
        )
    }
}

