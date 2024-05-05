package focus.launcher.two.screens

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import focus.launcher.two.logic.Agenda
import focus.launcher.two.logic.AppListViewModel
import focus.launcher.two.logic.CalText
import focus.launcher.two.logic.CalendarViewModel
import focus.launcher.two.logic.CameraText
import focus.launcher.two.logic.ClockText
import focus.launcher.two.logic.ContactText
import focus.launcher.two.logic.CurrentDateText
import focus.launcher.two.logic.Maps
import focus.launcher.two.logic.Phone
import focus.launcher.two.logic.PhotoApp

@Composable
fun HomeScreen(
    navController: NavController,
) {
    BackHandler(
        enabled = true,
        onBack = {
            navController.clearBackStack("appList")
            navController.clearBackStack("home")
            navController.clearBackStack("agenda")
        }
    )

    val vertThreshold = 20
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -vertThreshold) {
                        navController.navigate("appList")
                    }
                }
            }
    ) {

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .zIndex(1f)
    ) {
        Row(
            modifier = Modifier
                .weight(0.3f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                ClockText()
                CurrentDateText()
            }
        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .weight(0.3f),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Phone()
                ContactText()
                CameraText()
                PhotoApp()
                CalText()
                Maps()
            }
        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .weight(0.1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Apps",
                modifier = Modifier
                    .clickable {
                        navController.clearBackStack("home")
                        navController.navigate("appList")
                    }
            )
            Agenda(navController)


        }
    }
}
