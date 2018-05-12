package com.jereksel.libresubstratum.activities.legal

import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.main.MainView
import com.jereksel.libresubstratum.domain.PrivacyPolicySettings
import org.jetbrains.anko.find
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class LegalActivity: AppCompatActivity() {

    @Inject
    lateinit var privacyPolicySettings: PrivacyPolicySettings

    val spName = "legal"
    val spStringName = "accepted"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startMainActivity()

//
//        (application as App).getAppComponent(this).inject(this)
//
//        val sharedPreferences = getSharedPreferences(spName, MODE_PRIVATE)
//
//        val acc = sharedPreferences.getBoolean(spStringName, false)
//
//        if(!privacyPolicySettings.isPrivacyPolicyRequired() || acc) {
//            startMainActivity()
//        } else {
//
//            val dialog = AlertDialog.Builder(this)
//                    .setTitle("Privacy Policy")
//                    .setMessage(Html.fromHtml(resources.getString(R.string.privacy_policy_dialog)))
//                    .setCancelable(false)
//                    .setPositiveButton("OK") { _, _ ->
//                        sharedPreferences.edit().putBoolean(spStringName, true).apply()
//                        startMainActivity()
//                    }
//                    .show()
//
//            dialog.find<TextView>(android.R.id.message).movementMethod = LinkMovementMethod.getInstance()
//
//        }

    }

    private fun startMainActivity() {
        startActivity<MainView>()
        finish()
    }

}