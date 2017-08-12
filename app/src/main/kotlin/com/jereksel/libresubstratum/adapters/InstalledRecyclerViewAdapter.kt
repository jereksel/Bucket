package com.jereksel.libresubstratum.adapters

import android.content.Intent
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import butterknife.bindView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.installed.InstalledContract.Presenter
import com.jereksel.libresubstratum.adapters.InstalledRecyclerViewAdapter.ViewHolder
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.OverlayService
import kotlinx.android.synthetic.main.activity_installed.*

class InstalledRecyclerViewAdapter(
        val activity: AppCompatActivity,
        apps: List<InstalledOverlay>,
        val overlayService: OverlayService,
        val presenter: Presenter
): RecyclerView.Adapter<ViewHolder>() {

    val apps = apps.sortedBy { it.targetName }

    override fun getItemCount() = apps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val overlay = apps[position]

        val info = overlayService.getOverlayInfo(overlay.overlayId)

        holder.targetIcon.setImageDrawable(overlay.targetDrawable)
        holder.themeIcon.setImageDrawable(overlay.sourceThemeDrawable)
        holder.targetName.text = "${overlay.targetName} - ${overlay.sourceThemeName}"
        val color = if(info.enabled) Color.GREEN else Color.RED
        holder.targetName.setTextColor(color)

        holder.view.setOnClickListener {
            presenter.toggleOverlay(overlay.overlayId, !info.enabled)
            notifyItemChanged(position)
//            Snackbar.make(activity.recyclerView, "This change requires SystemUI restart", Snackbar.LENGTH_INDEFINITE)
//                    .setAction("Restart SystemUI", {overlayService.restartSystemUI()}).show()
        }

        holder.view.setOnLongClickListener {
            val targetApp = overlay.targetId
            val intent = activity.packageManager.getLaunchIntentForPackage(targetApp)

            if (intent != null) {
                intent
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
                activity.startActivity(Intent.createChooser(intent, "Split"));
            } else {
                Toast.makeText(activity, "Application cannot be opened", Toast.LENGTH_SHORT).show()
            }

            true
        }

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

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
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