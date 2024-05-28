package focus.launcher.two.logic

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import focus.launcher.two.screens.Appointment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CalendarViewModel : ViewModel() {
    var calendarInstances = mutableStateListOf<Appointment>()
    var calendarJob: Job? = null

    fun getCalendarInstances(context: Context) {
        calendarJob?.cancel()
        calendarJob = getCalendarEvents(context).onEach {
            calendarInstances.add(it)
        }.launchIn(viewModelScope)
    }

    fun getCalendarEvents(context: Context): Flow<Appointment> {
//            Map<Long, List<Appointment>> {
        val tempList = mutableListOf<Appointment>()
        val projection = arrayOf(
            CalendarContract.Instances.EVENT_ID,
            CalendarContract.Instances.TITLE,
            CalendarContract.Instances.BEGIN,
            CalendarContract.Instances.END,
            CalendarContract.Instances.START_DAY,
            CalendarContract.Instances.CALENDAR_COLOR,
        )

        val beginTime = Calendar.getInstance()
        val startMillis = beginTime.timeInMillis

        val endTime = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 15) // Add 15 days to the current date
        }
        val endMillis = endTime.timeInMillis

        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()

        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)

        val uri: Uri = builder.build()
        val selection =
            "${CalendarContract.Instances.BEGIN} >= ? AND ${CalendarContract.Instances.END} <= ? "

        val currentTime = Calendar.getInstance().timeInMillis
        val futureTime = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 15) // Add 15 days to the current date
        }.timeInMillis
        val selectionArgs = arrayOf(
            currentTime.toString(),
            futureTime.toString()
        )

        val sortOrder = "${CalendarContract.Instances.BEGIN} ASC"

        context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(CalendarContract.Instances.EVENT_ID)
            val titleIndex = cursor.getColumnIndex(CalendarContract.Instances.TITLE)
            val startDateIndex = cursor.getColumnIndex(CalendarContract.Instances.BEGIN)
            val endDateIndex = cursor.getColumnIndex(CalendarContract.Instances.END)
            val startDayIndex = cursor.getColumnIndex(CalendarContract.Instances.START_DAY)
            val colorIndex = cursor.getColumnIndex(CalendarContract.Instances.CALENDAR_COLOR)

            val titleList: MutableList<String> = mutableListOf()
            val dateList: MutableList<Long> = mutableListOf()

            while (cursor.moveToNext()) {
                val isCancelled = cursor.getInt(CalendarContract.Instances.STATUS_CANCELED)

                if (isCancelled == 1) {
                    continue
                } else {

                    val id = cursor.getLong(idIndex)
                    val title = cursor.getString(titleIndex) ?: "Unknown"
                    val startDate =
                        if (cursor.getLong(startDateIndex) == cursor.getLong(endDateIndex)) {
                            cursor.getLong(startDateIndex) + 86400000
                        } else {
                            cursor.getLong(startDateIndex)
                        }
                    val endDate = if (cursor.getLong(endDateIndex) < startDate) {
                        startDate
                    } else {
                        cursor.getLong(endDateIndex)
                    }
                    val startDay = cursor.getInt(startDayIndex)
                    val color = cursor.getInt(colorIndex)

                    if (title in titleList && startDate in dateList) {
                        titleList.add(title)
                        dateList.add(startDate)
                    } else {
                        tempList.add(
                            Appointment(
                                id = id,
                                title = title,
                                timeStart = formatTime(startDate),
                                timeEnd = formatTime(endDate),
                                startDay = ((startDay.toFloat() - 2440587.5) * 86400000).toLong(),
                                color = color
                            )
                        )
                        titleList.add(title)
                        dateList.add(startDate)
                    }
                }
            }

        }
        return tempList.asFlow()
    }


}

fun formatDate(dateInMillis: Long): String {
    val sdf = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    return sdf.format(Date(dateInMillis))
}

fun formatTime(dateInMillis: Long): String {
    val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
    return sdf.format(Date(dateInMillis))
}