package com.jereksel.changelogdialog

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import it.gmariotti.changelibs.library.internal.ChangeLogRecyclerViewAdapter
import it.gmariotti.changelibs.library.internal.ChangeLogRow
import it.gmariotti.changelibs.library.view.ChangeLogRecyclerView
import org.jire.kton.kton
import java.util.*

object ChangeLogDialog {

    fun show(context: Context, changeLog: ChangeLog, beta: Boolean) {

        val manager = context.packageManager

        val info = manager.getPackageInfo(
                context.packageName, 0)

        val code = info.versionCode

        val prefs = context.getSharedPreferences("ChangeLogDialog", MODE_PRIVATE)

        val lastCode = prefs.getInt("LAST_CODE", -1)

        if (code != lastCode) {

            val layoutInflater = context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val chgList = layoutInflater.inflate(R.layout.changelogdialog_dialog, null) as ChangeLogRecyclerView

            (chgList.adapter as ChangeLogRecyclerViewAdapter).add(changeLog)

            prefs.edit().putInt("LAST_CODE", code).apply()

            val alert = AlertDialog.Builder(context)
            alert.setTitle("Changelog")

            alert.setView(chgList)
            alert.setNegativeButton("Close", { dialog, _ -> dialog.dismiss() })
            alert.show()

        }

    }

    private fun ChangeLogRecyclerViewAdapter.add(changeLog: ChangeLog) {

        val rows = changeLog.versions.flatMap { version ->

            val header = ChangeLogRow()
            header.isHeader = true
            header.versionName = version.version
            header.changeDate = " "

            val rows = version.changes.map { change ->
                val row = ChangeLogRow()
                row.changeText = change
                row.isBulletedList = true
                row
            }

            listOf(header, *rows.toTypedArray())

        }

        add(LinkedList(rows))

    }

}

