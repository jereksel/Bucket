package com.jereksel.libresubstratum.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import butterknife.bindView
import com.futuremind.recyclerviewfastscroll.SectionTitleProvider
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.adapters.InstalledRecyclerViewAdapter.ViewHolder
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.OverlayInfo
import com.jereksel.libresubstratum.domain.OverlayService
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView.SectionedAdapter
import android.support.v4.content.ContextCompat.startActivity
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import com.jereksel.libresubstratum.BuildConfig


class InstalledRecyclerViewAdapter(
        val activity: AppCompatActivity,
        apps: List<InstalledOverlay>,
        val overlayService: OverlayService
): RecyclerView.Adapter<ViewHolder>(), SectionedAdapter {

    val apps = apps.sortedBy { it.targetName }

    override fun getItemCount() = apps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val overlay = apps[position]

        val info = overlayService.getOverlayInfo(overlay.overlayId)

//        holder.button.text = info.enabled.toString()
//        holder.button.setOnClickListener { toggle(position, info) }

        holder.targetIcon.setImageDrawable(overlay.targetDrawable)
        holder.themeIcon.setImageDrawable(overlay.sourceThemeDrawable)
        holder.targetName.text = "${overlay.targetName} - ${overlay.sourceThemeName}"
        val color = if(info.enabled) Color.GREEN else Color.RED
        holder.targetName.setTextColor(color)

        holder.view.setOnClickListener { toggle(position, info) }

        holder.view.setOnLongClickListener {
//            toggle(position, info)
//            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package: ${BuildConfig.APPLICATION_ID}"))
//            startActivity(holder.view.context, intent, null)
            val targetApp = overlay.targetId
            val intent = activity.packageManager.getLaunchIntentForPackage(targetApp)

            if (intent != null) {
                intent
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
                activity.startActivity(Intent.createChooser(intent, "Split"));
            } else {
                Toast.makeText(activity, "Not launchable actvity", Toast.LENGTH_SHORT).show()
            }

            true

//            val intent = Intent(android.provider.Settings.ACTION_SETTINGS)
//                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    .addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)

        }
//        holder.view.setOnLongClickListener {
////            toggle(position, info)
//            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package: ${BuildConfig.APPLICATION_ID}"))
//            startActivity(holder.view.context, intent, null)
//            true
//        }

        listOf(
                Triple(overlay.type1a, holder.type1a, R.string.theme_type1a_list),
                Triple(overlay.type1b, holder.type1b, R.string.theme_type1b_list),
                Triple(overlay.type1c, holder.type1c, R.string.theme_type1c_list),
                Triple(overlay.type2, holder.type2, R.string.theme_type2_list),
                Triple(overlay.type3, holder.type3, R.string.theme_type3_list)
        ).forEach { (name, view, stringId) ->
            if (!name.isNullOrEmpty()) {
                val text = view.context.getString(stringId)
                view.text = Html.fromHtml("<b>$text:</b> $name")
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_installed, parent, false)
        return ViewHolder(v)
    }

    override fun getSectionName(position: Int) = apps[position].targetName[0].toString()

    fun toggle(position: Int, info: OverlayInfo) {
        overlayService.toggleOverlay(info.overlayId, !info.enabled)
        notifyItemChanged(position)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
//        val button: Button by bindView(R.id.enable_button)
        val targetIcon: ImageView by bindView(R.id.target_icon)
        val themeIcon: ImageView by bindView(R.id.theme_icon)
        val targetName: TextView by bindView(R.id.target_name)
        val type1a: TextView by bindView(R.id.theme_type1a)
        val type1b: TextView by bindView(R.id.theme_type1b)
        val type1c: TextView by bindView(R.id.theme_type1c)
        val type2: TextView by bindView(R.id.theme_type2)
        val type3: TextView by bindView(R.id.theme_type3)
    }
}