package com.jereksel.libresubstratum.domain

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.graphics.drawable.Drawable
import com.jereksel.libresubstratum.data.Application
import java.io.File

class AppPackageManager(val context: Context) : IPackageManager {

    override fun getApplications(): List<Application> {
        val packages = context.packageManager.getInstalledPackages(GET_META_DATA)!!
        return packages
                .filter { it.applicationInfo.metaData != null }
                .map { Application(it.packageName, it.applicationInfo.metaData) }
    }

    override fun getHeroImage(appId: String): Drawable? {
        val res = context.packageManager.getResourcesForApplication(appId)
        val resId = res.getIdentifier("heroimage", "drawable", appId)
        if (resId == 0) {
            return null
        }
        return res.getDrawable(resId)
    }

    override fun getAppLocation(appId: String): File {
        return File(context.packageManager.getInstalledApplications(0)
                .find { it.packageName == appId }!!.sourceDir)
    }

    override fun isPackageInstalled(appId: String): Boolean {
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

    override fun stringIdToString(stringId: Int): String = context.getString(stringId)

    override fun getAppName(appId: String): String {
        val appInfo = context.packageManager.getApplicationInfo(appId, GET_META_DATA)
        return context.packageManager.getApplicationLabel(appInfo).toString()
    }

    override fun getAppIcon(appId: String): Drawable? {
        try {
            return context.packageManager.getApplicationIcon(appId)
        } catch (e: PackageManager.NameNotFoundException) {
            return null
        }
    }
}
