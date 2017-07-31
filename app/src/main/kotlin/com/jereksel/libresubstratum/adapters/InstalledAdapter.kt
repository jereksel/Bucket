package com.jereksel.libresubstratum.adapters

import android.content.Context
import android.content.om.OverlayInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.omslib.OMSLib

class InstalledAdapter(
        context: Context,
        apps: List<InstalledOverlay>
): ArrayAdapter<InstalledOverlay>(context, 0, apps.sortedBy { it.targetName })  {

    val oms = OMSLib.getOMS()!!

    override fun getView(position: Int, convertView_: View?, parent: ViewGroup): View {
        val overlay = getItem(position)
        val convertView = convertView_ ?: LayoutInflater.from(context).inflate(R.layout.item_installed, parent, false)

        // A user id to indicate the currently active user
        val info = oms.getOverlayInfo(overlay.overlayId, 0)
//        println(info)

        val button = convertView.findViewById(R.id.enable_button) as Button

        button.text = info.isEnabled.toString()

        button.setOnClickListener { toggle(info) }

        (convertView.findViewById(R.id.target_icon) as ImageView).setImageDrawable(overlay.targetDrawable)
        (convertView.findViewById(R.id.theme_icon) as ImageView).setImageDrawable(overlay.sourceThemeDrawable)
//        ((convertView.findViewById(R.id.target_name)) as TextView).text = "${overlay.overlayId} - ${overlay.targetId} - ${overlay.targetName} - ${overlay.sourceThemeName}"
        ((convertView.findViewById(R.id.target_name)) as TextView).text = "${overlay.targetName} - ${overlay.sourceThemeName}"

        return convertView
    }

    fun toggle(info: OverlayInfo) {
        oms.setEnabled(info.packageName, !info.isEnabled, 0, false)
        notifyDataSetInvalidated()
    }
}