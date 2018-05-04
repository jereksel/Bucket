/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.domain

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.res.ResourcesCompat
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.Application
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.data.InstalledTheme
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.extensions.has
import java.io.File
import java.util.concurrent.FutureTask

class AppPackageManager(val context: Context) : IPackageManager {

    val log = getLogger()

    companion object {
        private val SYSTEMUI = mapOf(
                "com.android.systemui.headers" to "Headers",
                "com.android.systemui.navbars" to "Navbars",
                "com.android.systemui.statusbars" to "Statusbars",
                "com.android.systemui.tiles" to "Tiles"
        )
        private val SETTINGS = mapOf(
                "com.android.settings.icons" to "Settings icons"
        )
    }

    val SUBSTRATUM_LEGACY = "Substratum_Legacy"
    val SUBSTRATUM_NAME = "Substratum_Name"
    val SUBSTRATUM_AUTHOR = "Substratum_Author"

    val metadataOverlayTarget = "Substratum_Target"
    val metadataOverlayParent = "Substratum_Parent"

    val metadataOverlayType1a = "Substratum_Type1a"
    val metadataOverlayType1b = "Substratum_Type1b"
    val metadataOverlayType1c = "Substratum_Type1c"
    val metadataOverlayType2 = "Substratum_Type2"
    val metadataOverlayType3 = "Substratum_Type3"

    val lock = java.lang.Object()

    override fun getInstalledOverlays(): List<InstalledOverlay> {
        return getApplications()
                .filter { it.metadata.has(metadataOverlayTarget) }
                .map {

                    log.debug("Reading information of overlay {}", it.appId)

                    val overlay = it.appId
                    val parent = it.metadata.getString(metadataOverlayParent)
                    val parentIcon = getAppIcon(parent)
                    val parentName = getAppName(parent)
                    val target = it.metadata.getString(metadataOverlayTarget)
                    val targetIcon = getAppIcon(target)
                    val targetName = getTargetName(overlay, target)

                    if (parentIcon == null) {
                        log.warn("Cannot read icon of {}", parent)
                    }

                    if (targetIcon == null) {
                        log.warn("Cannot read icon of {}", target)
                    }

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
                .filter { it.metadata.has(SUBSTRATUM_AUTHOR) }
                .filter { it.metadata.has(SUBSTRATUM_NAME) }
                .map {

                    log.debug("Reading information of theme {}", it.appId)

                    val name = it.metadata.getString(SUBSTRATUM_NAME)
                    val author = it.metadata.getString(SUBSTRATUM_AUTHOR)
                    val encrypted = it.metadata.getString("Substratum_Encryption") == "onCompileVerify"
                    val pluginVersion = it.metadata.getString("Substratum_Plugin")
                    InstalledTheme(it.appId, name, author, encrypted, pluginVersion, FutureTask { getHeroImage(it.appId) })
                }
                .toList()
    }

    override fun getInstalledTheme(appId: String) = synchronized(lock) {

        log.debug("Reading information of theme {}", appId)

        val application = packageManager.getApplicationInfo(appId, GET_META_DATA)

        val app = Application(appId, application.metaData)

        app.let {
            val name = it.metadata.getString(SUBSTRATUM_NAME)
            val author = it.metadata.getString(SUBSTRATUM_AUTHOR)
            val encrypted = it.metadata.getString("Substratum_Encryption") == "onCompileVerify"
            val pluginVersion = it.metadata.getString("Substratum_Plugin")
            InstalledTheme(it.appId, name, author, encrypted, pluginVersion, FutureTask { getHeroImage(it.appId) })
        }

    }

    val packageManager = context.packageManager

    fun getApplications(): Sequence<Application> {
        val packages = packageManager.getInstalledPackages(GET_META_DATA)!!
        return packages
                .asSequence()
                .filter { it.applicationInfo.metaData != null }
                .map { Application(it.packageName, it.applicationInfo.metaData) }
    }

    fun getHeroImage(appId: String): File? {

        val res = context.packageManager.getResourcesForApplication(appId)
        val resId = res.getIdentifier("heroimage", "drawable", appId)
        if (resId == 0) {
            return null
        }
        val drawable = ResourcesCompat.getDrawable(res, resId, null) ?: return null

        val bitmap = drawableToBitmap(drawable)

        return saveBitmap(bitmap, appId)
    }

    fun saveBitmap(bitmap: Bitmap, appId: String) : File {

        val file = File(context.cacheDir, "hero-$appId.jpg")
        if (file.exists()) {
            file.delete()
        }

        file.createNewFile()
        file.outputStream().use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
        }

        return file
    }

    //https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
    fun drawableToBitmap(drawable: Drawable): Bitmap {

        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        val bitmap: Bitmap

        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444) // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    override fun getAppLocation(appId: String): File = synchronized(lock) {

        if (SYSTEMUI.contains(appId)) {
            getAppLocation("com.android.systemui")
        } else if (SETTINGS.contains(appId)) {
                getAppLocation("com.android.settings")
        } else {
            File(context.packageManager.getApplicationInfo(appId, 0)!!.sourceDir)
        }

    }

    override fun isPackageInstalled(appId: String): Boolean {

        if (SYSTEMUI.contains(appId)) {
            return true
        }

        if (SETTINGS.contains(appId)) {
            return true
        }

        try {
            context.packageManager.getApplicationInfo(appId, 0)
            return true
        } catch (ignored: PackageManager.NameNotFoundException) {
            return false
        }
    }

    override fun getCacheFolder() = context.cacheDir

    fun stringIdToString(stringId: Int): String = context.getString(stringId)

    override fun getAppName(appId: String): String {

        if (SYSTEMUI.contains(appId)) {
            return SYSTEMUI[appId]!!
        }

        if (SETTINGS.contains(appId)) {
            return SETTINGS[appId]!!
        }

        try {
            val appInfo = context.packageManager.getApplicationInfo(appId, GET_META_DATA)
            return context.packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            return ""
        }
    }

    override fun getAppVersion(appId: String): Pair<Int, String> {
        val appData = context.packageManager.getPackageInfo(appId, GET_META_DATA)
        return appData.versionCode to appData.versionName
    }

    override fun getAppIcon(appId: String): Drawable? {

        if (SYSTEMUI.contains(appId)) {
            return getAppIcon("android")
        }

        if (SETTINGS.contains(appId)) {
            return getAppIcon("com.android.settings")
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
