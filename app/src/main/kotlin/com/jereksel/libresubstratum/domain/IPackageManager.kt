package com.jereksel.libresubstratum.domain

import android.graphics.drawable.Drawable
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.data.InstalledTheme
import java.io.File

interface IPackageManager {
    fun getInstalledThemes(): List<InstalledTheme>
    fun getInstalledTheme(appId: String): InstalledTheme
    fun getInstalledOverlays(): List<InstalledOverlay>
    fun getAppVersion(appId: String): Pair<Int, String>
    fun getAppIcon(appId: String): Drawable?
    fun getAppName(appId: String): String
    fun isPackageInstalled(appId: String): Boolean
    fun getAppLocation(appId: String): File
    fun getCacheFolder(): File
}
