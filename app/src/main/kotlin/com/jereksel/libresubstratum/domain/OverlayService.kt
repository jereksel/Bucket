package com.jereksel.libresubstratum.domain

interface OverlayService {
    fun enableOverlay(id: String)
    fun disableOverlay(id: String)
    fun getOverlayInfo(id: String): OverlayInfo
    fun toggleOverlay(id: String, enabled: Boolean) {
        if (enabled) {
            enableOverlay(id)
        } else {
            disableOverlay(id)
        }
    }
}