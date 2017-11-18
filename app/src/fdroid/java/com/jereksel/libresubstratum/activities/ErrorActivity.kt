package com.jereksel.libresubstratum.activities

/*
 * Copyright 2017 Andrzej Ressel
 * Copyright 2014-2017 Eduard Ereza Martínez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import cat.ereza.customactivityoncrash.R
import cat.ereza.customactivityoncrash.config.CaocConfig
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.domain.Metrics
import com.jereksel.libresubstratum.utils.CustomActivityOnCrashUtils
import kotlinx.android.synthetic.fdroid.fdroid_crash_activity.*
import org.jetbrains.anko.find
import javax.inject.Inject
import javax.inject.Named


class ErrorActivity : AppCompatActivity() {

    @Inject
    @field:Named("persistent")
    lateinit var metrics: Metrics

    @SuppressLint("PrivateResource")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as App).getAppComponent(this).inject(this)

        //This is needed to avoid a crash if the developer has not specified
        //an app-level theme that extends Theme.AppCompat
        val a = obtainStyledAttributes(R.styleable.AppCompatTheme)
        if (!a.hasValue(R.styleable.AppCompatTheme_windowActionBar)) {
            setTheme(R.style.Theme_AppCompat_Light_DarkActionBar)
        }
        a.recycle()

        setContentView(com.jereksel.libresubstratum.R.layout.fdroid_crash_activity)

        val restartButton = find<Button>(R.id.customactivityoncrash_error_activity_restart_button)

        val config = CustomActivityOnCrash.getConfigFromIntent(intent) as CaocConfig?

        if (config == null) {
            //This should never happen - Just finish the activity to avoid a recursive crash.
            finish()
            return
        }

        restartButton.setOnClickListener { CustomActivityOnCrash.restartApplication(this@ErrorActivity, config) }

        val moreInfoButton = findViewById<Button>(R.id.customactivityoncrash_error_activity_more_info_button)

        send_log_button.setOnClickListener {

            val message = CustomActivityOnCrash.getAllErrorDetailsFromIntent(this@ErrorActivity, intent)

            val fullMessage = message + "\nWhat were you doing when the crash happened?\n\n"

            val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "jereksel@gmail.com", null))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "LibreSubstratum bug report")
            emailIntent.putExtra(Intent.EXTRA_TEXT, fullMessage)
            startActivity(Intent.createChooser(emailIntent, "Send email..."))

        }

        moreInfoButton.setOnClickListener {
            //We retrieve all the error data and show it

            val dialog = AlertDialog.Builder(this@ErrorActivity)
                    .setTitle(R.string.customactivityoncrash_error_activity_error_details_title)
                    .setMessage(getErrorDetails())
                    .setPositiveButton(R.string.customactivityoncrash_error_activity_error_details_close, null)
                    .setNeutralButton(R.string.customactivityoncrash_error_activity_error_details_copy
                    ) { dialog, which -> copyErrorToClipboard() }
                    .show()
            val textView = dialog.findViewById<TextView>(android.R.id.message)
            textView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.customactivityoncrash_error_activity_error_details_text_size))
        }
    }

    private fun copyErrorToClipboard() {
        val errorInformation = getErrorDetails()

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?

        //Are there any devices without clipboard...?
        if (clipboard != null) {
            val clip = ClipData.newPlainText(getString(R.string.customactivityoncrash_error_activity_error_details_clipboard_label), errorInformation)
            clipboard.primaryClip = clip
            Toast.makeText(this@ErrorActivity, R.string.customactivityoncrash_error_activity_error_details_copied, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getErrorDetails() = CustomActivityOnCrashUtils.getAllErrorDetailsFromIntent(this, intent, metrics)
}
