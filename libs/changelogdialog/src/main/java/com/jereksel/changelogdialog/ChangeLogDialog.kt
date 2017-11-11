package com.jereksel.changelogdialog

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.support.v7.app.AlertDialog
import android.webkit.WebView

object ChangeLogDialog {

    fun show(context: Context, changeLog: ChangeLog, beta: Boolean) {

        val manager = context.packageManager

        val info = manager.getPackageInfo(
                context.packageName, 0)

        val code = info.versionCode

        val prefs = context.getSharedPreferences("ChangeLogDialog", MODE_PRIVATE)

        val lastCode = prefs.getInt("LAST_CODE", -1)

        if (code != lastCode) {

            prefs.edit().putInt("LAST_CODE", code).apply()

            val html = changeLog.versions
                    .filterNot { !beta && it.beta }
                    .map {
                        """
                        <div>
                        <h3>${it.version}</h3>
                        <ul>
                            ${it.changes.map { "<li>$it</li>" }.joinToString(separator = "")}
                        </ul>
                        </div>
                        """
                    }.joinToString(separator = "")

            val alert = AlertDialog.Builder(context)
            alert.setTitle("Changelog")

            val wv = WebView(context)
            wv.loadData(html, "text/html", null)

            alert.setView(wv)
            alert.setNegativeButton("Close", { dialog, _ -> dialog.dismiss() })
            alert.show()

        }

    }

}