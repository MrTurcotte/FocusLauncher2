package focus.launcher.two.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import focus.launcher.two.logic.AppListViewModel
import focus.launcher.two.logic.CalendarViewModel
import kotlin.jvm.Throws

class FocusBroadcastReceiver(
    viewModel: AppListViewModel,
    calendarViewModel: CalendarViewModel,
    context: Context
) : BroadcastReceiver() {
    private val appListViewModel = viewModel
    private val calendarModel = calendarViewModel
    private val receiverContext = context
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val packageName = intent.data?.schemeSpecificPart
            if (packageName != null) {
                Log.d("MyBroadcastReceiver", "Package changed: $packageName")
                appListViewModel.refreshApps()
            }
        }
        if (intent?.action == Intent.ACTION_PROVIDER_CHANGED) {
            calendarModel.getCalendarInstances(receiverContext)
        }
    }
}
