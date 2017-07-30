package com.jereksel.libresubstratum.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.InstalledOverlay

class InstalledAdapter(
        context: Context,
        apps: List<InstalledOverlay>
): ArrayAdapter<InstalledOverlay>(context, 0, apps.sortedBy { it.appName })  {

    override fun getView(position: Int, convertView_: View?, parent: ViewGroup): View {

        val overlay = getItem(position)
        val convertView = convertView_ ?: LayoutInflater.from(context).inflate(R.layout.item_installed, parent, false)


        return convertView

    }
}