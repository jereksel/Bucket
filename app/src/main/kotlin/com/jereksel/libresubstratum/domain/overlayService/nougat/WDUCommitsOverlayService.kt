package com.jereksel.libresubstratum.domain.overlayService.nougat

import android.content.Context

class WDUCommitsOverlayService(context: Context): WODUCommitsOverlayService(context) {
    override fun allPermissions() = super.allPermissions() + "projekt.interfacer.permission.ACCESS_SERVICE_INNER"
}
