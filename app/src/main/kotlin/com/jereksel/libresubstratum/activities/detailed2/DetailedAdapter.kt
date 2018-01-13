package com.jereksel.libresubstratum.activities.detailed2

import android.support.v7.util.DiffUtil
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.detailed2.DetailedViewState.Theme
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.data.Type2ExtensionToString
import com.jereksel.libresubstratum.extensions.list
import com.jereksel.libresubstratum.views.TypeView
import io.reactivex.subjects.BehaviorSubject
import kotterknife.bindView

class DetailedAdapter(
        var themes: List<Theme>,
        val detailedPresenter: DetailedPresenter
): RecyclerView.Adapter<DetailedAdapter.ViewHolder>() {

    val clicks = BehaviorSubject.create<DetailedAction>()

    override fun getItemCount() = themes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val theme = themes[position]

        holder.appId.text = theme.appId
        holder.appName.text = theme.name
        holder.appIcon.setImageDrawable(detailedPresenter.getAppIcon(theme.appId))

        if (theme.state == DetailedViewState.State.DEFAULT) {
            holder.overlay.visibility = GONE
        }

        holder.type1aSpinner((theme.type1a?.data ?: listOf()).map { Type1ExtensionToString(it) }, theme.type1a?.position ?: 0)
        holder.type1aView.onPositionChange { clicks.onNext(DetailedAction.ChangeSpinnerSelection.ChangeType1aSpinnerSelection(holder.adapterPosition, it)) }

        holder.type1bSpinner((theme.type1b?.data ?: listOf()).map { Type1ExtensionToString(it) }, theme.type1b?.position ?: 0)
        holder.type1bView.onPositionChange { clicks.onNext(DetailedAction.ChangeSpinnerSelection.ChangeType1bSpinnerSelection(holder.adapterPosition, it)) }

        holder.type1cSpinner((theme.type1c?.data ?: listOf()).map { Type1ExtensionToString(it) }, theme.type1c?.position ?: 0)
        holder.type1cView.onPositionChange { clicks.onNext(DetailedAction.ChangeSpinnerSelection.ChangeType1cSpinnerSelection(holder.adapterPosition, it)) }

        holder.type2Spinner((theme.type2?.data ?: listOf()).map { Type2ExtensionToString(it) }, theme.type1c?.position ?: 0)
        holder.type2Spinner.selectListener { clicks.onNext(DetailedAction.ChangeSpinnerSelection.ChangeType2SpinnerSelection(holder.adapterPosition, it)) }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_detailed, parent, false)
        return ViewHolder(v)
    }

    fun update(themes: List<Theme>) {
        val oldList = this.themes
        val newList = themes

        this.themes = themes
        DiffUtil.calculateDiff(ThemeDiffCallback(oldList, newList)).dispatchUpdatesTo(this)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val card: CardView by bindView(R.id.card_view)
        val checkbox: CheckBox by bindView(R.id.checkbox)

        val appName: TextView by bindView(R.id.appName)
        val appId: TextView by bindView(R.id.appId)
        val appIcon: ImageView by bindView(R.id.imageView)

        val upToDate: TextView by bindView(R.id.uptodate)

        val type1aView: TypeView by bindView(R.id.type1aview)
        val type1bView: TypeView by bindView(R.id.type1bview)
        val type1cView: TypeView by bindView(R.id.type1cview)

        val type2Spinner: Spinner by bindView(R.id.spinner_2)

        val overlay: RelativeLayout by bindView(R.id.overlay)

        fun type1aSpinner(list: List<Type1ExtensionToString>, position: Int) {
            if (list.isEmpty()) {
                type1aView.visibility = GONE
            } else {
                type1aView.visibility = VISIBLE
                type1aView.setType1(list)
                type1aView.setSelection(position)
            }
        }

        fun type1bSpinner(list: List<Type1ExtensionToString>, position: Int) {
            if (list.isEmpty()) {
                type1bView.visibility = GONE
            } else {
                type1bView.visibility = VISIBLE
                type1bView.setType1(list)
                type1bView.setSelection(position)
            }
        }


        fun type1cSpinner(list: List<Type1ExtensionToString>, position: Int) {
            if (list.isEmpty()) {
                type1cView.visibility = GONE
            } else {
                type1cView.visibility = VISIBLE
                type1cView.setType1(list)
                type1cView.setSelection(position)
            }
        }

        fun type2Spinner(list: List<Type2ExtensionToString>, position: Int) {
            if (list.isEmpty()) {
                type2Spinner.visibility = GONE
            } else {
                type2Spinner.visibility = VISIBLE
                type2Spinner.list = list
                type2Spinner.setSelection(position)
            }
        }

    }

    private fun Spinner.selectListener(fn: (Int) -> Unit) {

        var user = false

        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (user) {
                    fn(position)
                } else {
                    user = true
                }
            }
        }
    }

    class ThemeDiffCallback(
            private val oldList: List<Theme>,
            private val newList: List<Theme>
    ): DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition].appId == newList[newItemPosition].appId

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition] == newList[newItemPosition]
    }

}