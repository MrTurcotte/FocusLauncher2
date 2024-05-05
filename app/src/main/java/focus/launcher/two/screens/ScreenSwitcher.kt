package focus.launcher.two.screens

import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import focus.launcher.two.logic.AppListViewModel
import focus.launcher.two.logic.CalendarViewModel


@Composable
fun ScreenSwitcher(
    navController: NavHostController,
    context: Context,
    viewModel: AppListViewModel,
    calViewModel: CalendarViewModel,
) {

    val duration = 300

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        composable(
            route = "home",
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        duration, easing = LinearEasing
                    )
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        duration, easing = LinearEasing
                    )
                )
            }
        ) {
            HomeScreen(
                navController = navController,
            )
        }
        composable(
            route = "appList",
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        duration, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(duration, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Up
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        duration, easing = LinearEasing
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(500, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.Down
                )
            }
        ) {
            AppList(
                context = context,
                appListViewModel = viewModel,
                navController = navController,

                )
        }
        composable(
            route = "agenda",
            enterTransition = {
                fadeIn(
                    animationSpec = tween(
                        duration, easing = LinearEasing
                    )
                ) + slideIntoContainer(
                    animationSpec = tween(duration, easing = EaseIn),
                    towards = AnimatedContentTransitionScope.SlideDirection.Start
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        duration, easing = LinearEasing
                    )
                ) + slideOutOfContainer(
                    animationSpec = tween(500, easing = EaseOut),
                    towards = AnimatedContentTransitionScope.SlideDirection.End
                )
            }
        ) {
            CalendarAgenda(
                calViewModel,
                navController
            )
        }
    }
}