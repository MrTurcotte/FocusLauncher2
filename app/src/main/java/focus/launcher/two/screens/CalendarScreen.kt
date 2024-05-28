package focus.launcher.two.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import focus.launcher.two.logic.CalendarViewModel
import focus.launcher.two.logic.formatDate
import java.util.*
import kotlin.collections.List

// Dummy data class for appointment
data class Appointment(
    val id: Long,
    val title: String,
    val timeStart: String,
    val timeEnd: String,
    val startDay: Long,
    val color: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CalendarAgenda(
    calendarViewModel: CalendarViewModel,
    navController: NavController
) {
    val context = LocalContext.current

//    LaunchedEffect(key1 = true) {
//        calendarViewModel.getCalendarInstances(context)
//
//    }

    BackHandler(
        true,
        onBack = {
            navController.navigate("home")
        }
    )

    val appointments = calendarViewModel.calendarInstances
    val sortedAppointments = appointments.groupBy { it.startDay }.toSortedMap()


    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        sortedAppointments.forEach { (date, appointment) ->
            stickyHeader {
                CalendarHeader(date)
            }

            items(appointment) { event ->
                Box(
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    AgendaItem(appointment = event, navController = navController)
                }
            }
        }

    }
}

@Composable
fun AgendaItem(appointment: Appointment, navController: NavController) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(5.dp)
            .clickable {

                navController.navigate("home")

                val intent = Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("content://com.android.calendar/events/" + appointment.id))
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }


            }
            .fillMaxWidth(),
        border = BorderStroke(2.dp, Color(appointment.color)),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Text(
                text = if (appointment.timeStart == appointment.timeEnd) {
                    "All Day"
                } else {
                    appointment.timeStart + " - " + appointment.timeEnd
                       },
                style = MaterialTheme.typography.bodyMedium)
            Text(text = appointment.title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun CalendarHeader(dateLong: Long) {
    val date = formatDate(dateLong)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .height(32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = date,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(4.dp)
        )
    }
}