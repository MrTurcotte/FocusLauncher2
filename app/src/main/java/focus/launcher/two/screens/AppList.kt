package focus.launcher.two.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.ColorMatrix
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import focus.launcher.two.logic.AppListViewModel

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("QueryPermissionsNeeded")
@Composable
fun AppList(
    context: Context,
    appListViewModel: AppListViewModel,
    navController: NavHostController
) {
    val unsortedApps = appListViewModel.unsortedAppList

    val packageManager = context.packageManager
    val matrix = ColorMatrix()
    matrix.setSaturation(0F)
    val sortValue = appListViewModel.appSort

    val sorter =
        if (sortValue == 0) {
            unsortedApps.groupBy { it.appName[0].lowercase() }.toSortedMap()
        } else {
            unsortedApps.groupBy { it.appCategory.lowercase() }.toSortedMap()
        }


    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult(), onResult = {
        })


    BackHandler(
        true,
        onBack = {
            navController.navigate("home")
            navController.clearBackStack("appList")
//            appListViewModel.refreshApps()
        }
    )

    Column {
        Row {
            SortButton(
                appListViewModel = appListViewModel,
                sortValue = sortValue,
                newSortValue = 0,
                textString = "Name"
            )
            SortButton(
                appListViewModel = appListViewModel,
                sortValue = sortValue,
                newSortValue = 1,
                textString = "Category"
            )
        }


        Row {
            AnimatedContent(
                label = "LazyColumn",
                targetState = sortValue,
                transitionSpec = {
                    if (sortValue == 0) {
                        slideInHorizontally(
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMediumLow,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            ),
                            initialOffsetX = { -it }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMediumLow,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            ),
                            targetOffsetX = { it }
                        )
                    } else {
                        slideInHorizontally(
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMediumLow,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            ),
                            initialOffsetX = { it }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = spring(
                                stiffness = Spring.StiffnessMediumLow,
                                visibilityThreshold = IntOffset.VisibilityThreshold
                            ),
                            targetOffsetX = { -it }
                        )
                    }
                }
            ) { unUsedVariable ->

                val temp = unUsedVariable

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.Start,
                ) {
                    sorter.forEach { (initial, appList) ->
                        stickyHeader {
                            CharacterHeader(initial = initial)
                        }

                        items(
                            items = appList,
                            key = { it.id }
                        ) { appInfo ->
                            val icon = appInfo.appIcon
                            val appName = appInfo.appName
                            val packageName = appInfo.packageName
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {

                                Image(
                                    icon,
                                    contentDescription = "$appName Icon",
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(24.dp),
                                    colorFilter = ColorFilter.colorMatrix(
                                        androidx.compose.ui.graphics.ColorMatrix(
                                            floatArrayOf(
                                                0.33f, 0.33f, 0.33f, 0f, 0f,
                                                0.33f, 0.33f, 0.33f, 0f, 0f,
                                                0.33f, 0.33f, 0.33f, 0f, 0f,
                                                0f, 0f, 0f, 1f, 0f
                                            )
                                        )
                                    )
                                )
                                Text(
                                    text = appName,
                                    modifier = Modifier
                                        .animateItemPlacement()
                                        .padding(8.dp)
                                        .combinedClickable(
                                            onClick = {
                                                navController.navigate("home")
                                                navController.clearBackStack("appList")
//                                                appListViewModel.refreshApps()
                                                val launchIntent =
                                                    packageManager.getLaunchIntentForPackage(
                                                        packageName
                                                    )
                                                launchIntent?.let {
                                                    context.startActivity(it)
                                                }
                                            },
                                            onLongClick = {
                                                val uninstallIntent = Intent(Intent.ACTION_DELETE)
                                                uninstallIntent.data =
                                                    Uri.parse("package:${packageName}")
                                                launcher.launch(uninstallIntent)
                                            },
                                            onLongClickLabel = "Uninstall App"


                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CharacterHeader(initial: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = initial.uppercase(),
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(4.dp)
        )
    }
}

@Composable
fun SortButton(
    appListViewModel: AppListViewModel,
    sortValue: Int,
    newSortValue: Int,
    textString: String
) {
    Button(
        onClick = {
            appListViewModel.changeAppSort(newSortValue)
        },
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.DarkGray
        )
    ) {
        Text(
            text = textString,
            textDecoration = if (sortValue == 1 && textString == "Category" || sortValue == 0 && textString == "Name") {
                TextDecoration.Underline
            } else {
                TextDecoration.None
            },
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
        )
    }
}