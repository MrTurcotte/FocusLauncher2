package focus.launcher.two.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import focus.launcher.two.logic.AppListViewModel

class FocusBroadcastReceiver(viewModel: AppListViewModel) : BroadcastReceiver() {
    private val appListViewModel = viewModel
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val packageName = intent.data?.schemeSpecificPart
            if (packageName != null) {
                Log.d("MyBroadcastReceiver", "Package changed: $packageName")
                appListViewModel.refreshApps()
            }
        }
    }
}
