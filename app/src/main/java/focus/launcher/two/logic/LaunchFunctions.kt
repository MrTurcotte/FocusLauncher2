package focus.launcher.two.logic

import android.content.Intent
import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.provider.AlarmClock
import android.provider.ContactsContract
import android.provider.MediaStore
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import java.util.Date
import java.util.Locale

@Composable
fun Phone() {
    val context = LocalContext.current

    Text(
        text = "Phone",
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_DIAL)
                context.startActivity(intent)
            }
    )
}

@Composable
fun Maps() {
    val context = LocalContext.current

    Text(
        text = "Maps",
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                val locationUri = "geo:0,0?q="
                val intent = Intent(Intent.ACTION_VIEW, locationUri.toUri())
                context.startActivity(intent)
            }
    )
}

@Composable
fun PhotoApp() {
    val context = LocalContext.current

    Text(
        text = "Photos",
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.type = "image/*"
                context.startActivity(intent)
            }
    )
}

@Composable
fun ContactText() {
    val context = LocalContext.current
    Text(
        text = "Contacts",
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                val contactsIntent =
                    Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI)
                context.startActivity(contactsIntent)
            }
    )

}

@Composable
fun CameraText() {
    val context = LocalContext.current
    Text(
        text = "Camera",
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                context.startActivity(cameraIntent)
            }
    )

}

@Composable
fun ClockText() {
    val context = LocalContext.current
    val clockFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val currentTime = Calendar.getInstance().time
    var formattedTime by remember { mutableStateOf(clockFormat.format(currentTime)) }

    LaunchedEffect(true) {
        while (true) {
            delay(1000)
            formattedTime = getFormattedTime()
        }
    }

    Text(
        text = formattedTime,
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                val clockIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS)
                context.startActivity(clockIntent)
            },
        fontSize = 60.sp
    )
}

private fun getFormattedTime(): String {
    val currentTime = System.currentTimeMillis()
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    return timeFormat.format(currentTime)
}

@Composable
fun CurrentDateText() {
    val currentDate = remember { mutableStateOf(getFormattedDate()) }
    val calendar = Calendar.getInstance()
    calendar.apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val midnight = calendar.timeInMillis
    val currentTime = System.currentTimeMillis()
    val delayMillis = midnight - currentTime

    LaunchedEffect(Unit) {
        delay(delayMillis)
        currentDate.value = getFormattedDate()
    }

    Text(
        text = currentDate.value,
        style = TextStyle(fontSize = 16.sp)
    )
}

fun getFormattedDate(): String {
    val dateFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault())
    return dateFormat.format(Date())
}

@Composable
fun CalText() {
    val context = LocalContext.current
    Text(
        text = "Calendar",
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                val calendarIntent = Intent(Intent.ACTION_MAIN).apply {
                    addCategory(Intent.CATEGORY_APP_CALENDAR)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(calendarIntent)
            }
    )

}

@Composable
fun Agenda(navController: NavController) {
    Text(
        text = "Agenda",
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                navController.clearBackStack("home")
                navController.navigate("agenda")
            }
    )
}

