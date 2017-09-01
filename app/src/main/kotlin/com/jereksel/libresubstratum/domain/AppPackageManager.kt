package com.jereksel.libresubstratum.domain

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.main.MainPresenter
import com.jereksel.libresubstratum.data.Application
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.extensions.has
import java.io.File
import java.util.logging.Logger

class AppPackageManager(val context: Context) : IPackageManager {

    companion object {
        private val SYSTEMUI = mapOf(
                "com.android.systemui.headers" to "Headers",
                "com.android.systemui.navbars" to "Navbars",
                "com.android.systemui.statusbars" to "Statusbars",
                "com.android.systemui.tiles" to "Tiles"
        )
    }

    val metadataOverlayTarget = "Substratum_Target"
    val metadataOverlayParent = "Substratum_Parent"

    val metadataOverlayType1a = "Substratum_Type1a"
    val metadataOverlayType1b = "Substratum_Type1b"
    val metadataOverlayType1c = "Substratum_Type1c"
    val metadataOverlayType2 = "Substratum_Type2"
    val metadataOverlayType3 = "Substratum_Type3"

    override fun getInstalledOverlays(): List<InstalledOverlay> {
        return getApplications()
                .filter { it.metadata.has(metadataOverlayTarget) }
                .map {
                    val overlay = it.appId
                    val parent = it.metadata.getString(metadataOverlayParent)
                    val parentIcon = getAppIcon(parent)!!
                    val parentName = getAppName(parent)
                    val target = it.metadata.getString(metadataOverlayTarget)
                    val targetIcon = getAppIcon(target)!!
                    val targetName = getTargetName(overlay, target)

                    val type1a = it.metadata.getString(metadataOverlayType1a)
                    val type1b = it.metadata.getString(metadataOverlayType1b)
                    val type1c = it.metadata.getString(metadataOverlayType1c)
                    val type2 = it.metadata.getString(metadataOverlayType2)
                    val type3 = it.metadata.getString(metadataOverlayType3)
                    InstalledOverlay(overlay, parent, parentName, parentIcon, target, targetName,
                            targetIcon, type1a, type1b, type1c, type2, type3)
                }
                .toList()
    }

    override fun getInstalledThemes(): List<InstalledTheme> {
        return getApplications()
                .filter { it.metadata.has(MainPresenter.SUBSTRATUM_AUTHOR) }
                .filter { it.metadata.has(MainPresenter.SUBSTRATUM_NAME) }
                .map {
                    val name = it.metadata.getString(MainPresenter.SUBSTRATUM_NAME)
                    val author = it.metadata.getString(MainPresenter.SUBSTRATUM_AUTHOR)
                    val heroImage = getHeroImage(it.appId)
                    InstalledTheme(it.appId, name, author, heroImage)
                }
                .toList()
    }

    fun getApplications(): Sequence<Application> {
        val packages = context.packageManager.getInstalledPackages(GET_META_DATA)!!
        return packages
                .asSequence()
                .filter { it.applicationInfo.metaData != null }
                .map { Application(it.packageName, it.applicationInfo.metaData) }
    }

    fun getHeroImage(appId: String): Drawable? {
        val res = context.packageManager.getResourcesForApplication(appId)
        val resId = res.getIdentifier("heroimage", "drawable", appId)
        if (resId == 0) {
            return null
        }
        return ResourcesCompat.getDrawable(res, resId, null)
    }

    override fun getAppLocation(appId: String): File {
        return File(context.packageManager.getInstalledApplications(0)
                .find { it.packageName == appId }!!.sourceDir)
    }

    override fun isPackageInstalled(appId: String): Boolean {

        if (SYSTEMUI.contains(appId)) {
            return true
        }

        try {
            context.packageManager.getApplicationInfo(appId, 0)
            return true
        } catch (ignored: PackageManager.NameNotFoundException) {
            return false
        }
    }

    override fun getCacheFolder(): File {
        return context.cacheDir
    }

    fun stringIdToString(stringId: Int): String = context.getString(stringId)

    override fun getAppName(appId: String): String {

        if (SYSTEMUI.contains(appId)) {
            return SYSTEMUI[appId]!!
        }


        val appInfo = context.packageManager.getApplicationInfo(appId, GET_META_DATA)
        return context.packageManager.getApplicationLabel(appInfo).toString()
    }

    override fun getAppVersion(appId: String): Pair<Int, String> {
        val appData = context.packageManager.getPackageInfo(appId, GET_META_DATA)
        return appData.versionCode to appData.versionName
    }

    override fun getAppIcon(appId: String): Drawable? {

        if (SYSTEMUI.contains(appId)) {
            return getAppIcon("android")
        }

        try {
            return context.packageManager.getApplicationIcon(appId)
        } catch (e: PackageManager.NameNotFoundException) {
            return null
        }
    }

    private fun getTargetName(overlayId: String, targetId: String) =
            when {
                overlayId.startsWith("com.android.systemui.navbars") -> stringIdToString(R.string.systemui_navigation)
                overlayId.startsWith("com.android.systemui.headers") -> stringIdToString(R.string.systemui_headers)
                overlayId.startsWith("com.android.systemui.tiles") -> stringIdToString(R.string.systemui_qs_tiles)
                overlayId.startsWith("com.android.systemui.statusbars") -> stringIdToString(R.string.systemui_statusbar)
                overlayId.startsWith("com.android.settings.icons") -> stringIdToString(R.string.settings_icons)
                else -> getAppName(targetId)
            }
}
