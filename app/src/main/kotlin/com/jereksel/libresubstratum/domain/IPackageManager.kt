package com.jereksel.libresubstratum.domain

import android.graphics.drawable.Drawable
import com.jereksel.libresubstratum.data.Application

interface IPackageManager {
    fun getApplications(): List<Application>
    fun getHeroImage(appId: String): Drawable?
}
