package focus.launcher.two.data

import com.google.accompanist.drawablepainter.DrawablePainter
import java.util.UUID

data class AppWithID(
    val id: UUID,
    val packageName: String,
    val appName: String,
    val appIcon: DrawablePainter,
    val appCategory: String
)

