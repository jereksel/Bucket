package com.jereksel.libresubstratum.adapters

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import butterknife.bindView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.extensions.list
import com.jereksel.libresubstratumlib.ThemePack

class ThemePackAdapter(
        themePack : ThemePack,
        val packageManager: IPackageManager
) : RecyclerView.Adapter<ThemePackAdapter.ViewHolder>() {

    val themePack: ThemePack = ThemePack(themePack.themes.sortedBy { packageManager.getAppName(it.application) }, themePack.type3)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = themePack.themes[position]
        val appId = theme.application
        holder.appName.text = packageManager.getAppName(appId)
        holder.appId.text = theme.application
        holder.appIcon.setImageDrawable(packageManager.getAppIcon(appId))
        holder.card.setOnClickListener { holder.checkbox.performClick() }

        holder.type1Spinners.zip(theme.type1).forEach { (spinner, type1) ->
            spinner.visibility = VISIBLE
            spinner.list = type1.extension.map(::Type1ExtensionToString)
        }

        val type2 = theme.type2
        if (type2 != null) {
            val spinner = holder.type2Spinner
            spinner.visibility = VISIBLE
            spinner.list = type2.extensions.map { it.name }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_detailed, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = themePack.themes.size

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        val card: CardView by bindView(R.id.card_view)
        val checkbox: CheckBox by bindView(R.id.checkbox)

        val appName: TextView by bindView(R.id.appName)
        val appId: TextView by bindView(R.id.appId)
        val appIcon: ImageView by bindView(R.id.imageView)

        val type1aSpinner: Spinner by bindView(R.id.spinner_1a)
        val type1bSpinner: Spinner by bindView(R.id.spinner_1b)
        val type1cSpinner: Spinner by bindView(R.id.spinner_1c)
        val type1Spinners = listOf(type1aSpinner, type1bSpinner, type1cSpinner)

        val type2Spinner: Spinner by bindView(R.id.spinner_2)
    }

}
