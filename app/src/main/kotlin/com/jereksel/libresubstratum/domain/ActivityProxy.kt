package com.jereksel.libresubstratum.domain

import android.app.Application
import android.content.Intent

class ActivityProxy(val app: Application): IActivityProxy {

    override fun openActivityInSplit(appId: String): Boolean {
        val intent = app.packageManager.getLaunchIntentForPackage(appId)

        if (intent != null) {
            intent
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
            app.startActivity(Intent.createChooser(intent, "Split"));
            return true
        } else {
            return false
        }
    }
}