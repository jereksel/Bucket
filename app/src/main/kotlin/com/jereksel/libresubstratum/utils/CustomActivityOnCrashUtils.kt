package com.jereksel.libresubstratum.utils

import android.content.Context
import android.content.Intent
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.jereksel.libresubstratum.domain.Metrics

object CustomActivityOnCrashUtils {

    fun getAllErrorDetailsFromIntent(context: Context, intent: Intent, metrics: Metrics): String {

        val messageBuilder = StringBuilder()

        if (metrics.getMetrics().isNotEmpty()) {
            messageBuilder.append("Metrics:\n")
        }

        metrics.getMetrics().forEach { key, value ->
            messageBuilder.append("$key: $value\n")
        }

        val details = CustomActivityOnCrash.getAllErrorDetailsFromIntent(context, intent)

        messageBuilder.append(details)

        return messageBuilder.toString()

    }

}
