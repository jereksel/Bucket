package com.jereksel.libresubstratum.domain

data class OverlayInfo(
        val overlayId: String,
        val targetId: String,
        val enabled: Boolean
)