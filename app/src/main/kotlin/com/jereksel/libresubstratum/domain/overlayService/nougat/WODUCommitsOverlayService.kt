package com.jereksel.libresubstratum.domain.overlayService.nougat

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings

open class WODUCommitsOverlayService(context: Context): InterfacerOverlayService(context) {
    override fun allPermissions() = listOf(WRITE_EXTERNAL_STORAGE)

    override fun additionalSteps(): String? {

        try {
            context.packageManager.getPackageInfo("projekt.substratum", 0)
        } catch (e: PackageManager.NameNotFoundException) {
            return "Please install Substratum app (it's required for Interfacer to function properly)"
        }

        val areAllPackagesAllowed = Settings.Secure.getInt(context.contentResolver, "force_authorize_substratum_packages") == 1

        return if (!areAllPackagesAllowed) {
            """Please turn on "Force authorize every theme app" in developer settings"""
        } else {
            null
        }
    }
}