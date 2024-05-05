package focus.launcher.two.logic


import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.drawablepainter.DrawablePainter
import focus.launcher.two.data.AppWithID
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.UUID

class AppListViewModel(context: Context, intent: Intent) : ViewModel() {

    private val appContext = context
    private val appIntent = intent
    private var getAppsJob: Job? = null

    val unsortedAppList: MutableList<AppWithID> = mutableStateListOf<AppWithID>()
    var appSort by mutableStateOf(0)

    init {
        getApps()
    }

    private fun getApps() {
        unsortedAppList.clear()
        getAppsJob?.cancel()
        getAppsJob = ListEvents(context = appContext, intent = appIntent).getList().onEach { app ->
            unsortedAppList.add(app)
        }.launchIn(viewModelScope)
    }

    fun refreshApps() {
        getApps()
    }

    fun changeAppSort(sortValue: Int) {
        appSort = sortValue
    }
}

class ListEvents(context: Context, intent: Intent) {

    private val appContext = context
    private val appIntent = intent
    private val pm: PackageManager = appContext.packageManager

    fun getList(): Flow<AppWithID> {
        val buildList = mutableListOf<AppWithID>()
        val tempList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                appIntent,
                PackageManager.ResolveInfoFlags.of(0L)
            )
        } else {
            pm.queryIntentActivities(appIntent, 0)
        }

        tempList.forEach {
            val appIcon = DrawablePainter(it.activityInfo.loadIcon(pm))
            val packageName = it.activityInfo.packageName
            val appName = if (it.activityInfo.labelRes != 0) {
                pm.getResourcesForApplication(it.activityInfo.applicationInfo)
                    .getString(it.activityInfo.labelRes)
            } else {
                it.activityInfo.applicationInfo.loadLabel(pm).toString()
            }
            val applicationCategory = pm.getApplicationInfo(packageName, 0).category
            val category =
                if (ApplicationInfo.getCategoryTitle(appContext, applicationCategory) == null) {
                    "Unknown Category"
                } else {
                    ApplicationInfo.getCategoryTitle(appContext, applicationCategory).toString()
                }

            buildList.add(
                AppWithID(
                    id = UUID.randomUUID(),
                    packageName = packageName,
                    appName = appName,
                    appIcon = appIcon,
                    appCategory = category
                )

            )
        }

          return buildList.sortedBy { it.appName.lowercase() }.asFlow()
    }
}

