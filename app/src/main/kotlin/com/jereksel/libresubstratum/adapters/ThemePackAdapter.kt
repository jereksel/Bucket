package com.jereksel.libresubstratum.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import kotterknife.bindView
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.data.Type2ExtensionToString
import com.jereksel.libresubstratum.extensions.list
import com.jereksel.libresubstratum.views.ColorView
import io.reactivex.rxkotlin.toFlowable
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import org.adw.library.widgets.discreteseekbar.internal.PopupIndicator

class ThemePackAdapter(
        val presenter: Presenter
) : RecyclerView.Adapter<ThemePackAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        presenter.setAdapterView(position, holder)

        holder.card.setOnClickListener {
            holder.checkbox.toggle()
        }

        holder.card.setOnLongClickListener {
            presenter.compileAndRun(holder.adapterPosition)
            true
        }

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            presenter.setCheckbox(holder.adapterPosition, isChecked)
        }

        holder.type1aSpinner.selectListener { spinnerPosition ->
            presenter.setType1a(holder.adapterPosition, spinnerPosition)
        }
        holder.type1bSpinner.selectListener { spinnerPosition ->
            presenter.setType1b(holder.adapterPosition, spinnerPosition)
        }
        holder.type1cSpinner.selectListener { spinnerPosition ->
            presenter.setType1c(holder.adapterPosition, spinnerPosition)
        }
        holder.type2Spinner.selectListener { spinnerPosition ->
            presenter.setType2(holder.adapterPosition, spinnerPosition)
        }

        holder.appIcon.setOnLongClickListener { presenter.openInSplit(holder.adapterPosition); true }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_detailed, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount() = presenter.getNumberOfThemes()

//    override fun getItemCount() = themePack.themes.size

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view), ThemePackAdapterView {

        val card: CardView by bindView(R.id.card_view)
        val checkbox: CheckBox by bindView(R.id.checkbox)

        val appName: TextView by bindView(R.id.appName)
        val appId: TextView by bindView(R.id.appId)
        val appIcon: ImageView by bindView(R.id.imageView)

        val upToDate: TextView by bindView(R.id.uptodate)

        val type1aSpinner: Spinner by bindView(R.id.spinner_1a)
        val colorview1a: ColorView by bindView(R.id.colorview_1a)
        val type1aSeekbar: SeekBar by bindView(R.id.seekBar_1a)

        val type1bSpinner: Spinner by bindView(R.id.spinner_1b)
        val type1cSpinner: Spinner by bindView(R.id.spinner_1c)
        val type1Spinners = listOf(type1aSpinner, type1bSpinner, type1cSpinner)

        val type2Spinner: Spinner by bindView(R.id.spinner_2)

        val overlay: RelativeLayout by bindView(R.id.overlay)
//        init {
//            (type1Spinners + type2Spinner).forEach { it.visibility = View.GONE }
//        }

        override fun setAppId(id: String) {
            appId.text = id
        }

        override fun setAppName(name: String) {
            appName.setTextColor(Color.BLACK)
            appName.text = name
        }

        override fun setAppIcon(icon: Drawable?) {
            appIcon.setImageDrawable(icon)
        }

        override fun setCheckbox(checked: Boolean) {
//            checkbox.setOnCheckedChangeListener(null)
            if (checkbox.isChecked != checked) {
                checkbox.isChecked = checked
            }
        }

        override fun type1aSpinner(list: List<Type1ExtensionToString>, position: Int) {
//            type1aSpinner.onItemSelectedListener = null
            if (list.isEmpty()) {
                colorview1a.visibility = GONE
                type1aSpinner.visibility = GONE
                type1aSeekbar.visibility = GONE
            } else {
                type1aSpinner.visibility = VISIBLE
                colorview1a.visibility = VISIBLE
                type1aSeekbar.visibility = VISIBLE
                type1aSpinner.adapter = Type1SpinnerArrayAdapter(type1aSpinner.context, list)
                type1aSpinner.setSelection(position)
                val colors = list.sortedWith(compareBy({ !it.type1.default }, {
                    val hsv = FloatArray(3)
                    val rgb = it.type1.color
                    if (rgb.isEmpty()) {
                        Float.MAX_VALUE
                    } else {
                        Color.colorToHSV(Color.parseColor(rgb), hsv)
                        hsv[0]
                    }
                })).map { it.type1.color }.map { if (it.isNotEmpty()) { it } else {"white"} }.map { Color.parseColor(it) }
                colorview1a.colors = colors
                colorview1a.invalidate()

                type1aSeekbar.post {

                    val width = type1aSeekbar.measuredWidth

                    val margin = ((width.toFloat()/colors.size)/2).toInt()

                    type1aSeekbar.setPadding(margin, 0, margin, 0);
                    type1aSeekbar.progressDrawable = ColorDrawable(Color.TRANSPARENT)
//                type1aSeekbar.(margin, 0, margin, 0);

                    type1aSeekbar.max = colors.size - 1

                    type1aSeekbar.invalidate()
                }

                type1aSeekbar.progress = position

                type1aSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        type1aSpinner.setSelection(progress)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    }

                })


/*
                discrete1a.min = 0
                discrete1a.max = a.size - 1

                discrete1a.setOnProgressChangeListener(object: DiscreteSeekBar.OnProgressChangeListener {
                    override fun onProgressChanged(seekBar: DiscreteSeekBar, value: Int, fromUser: Boolean) {
//                        seekBar.setRippleColor(a[value])
//                        seekBar.setTracColor(a[value])
                        val color = a[value]
//                        seekBar.setIndicatorPopupEnabled(false)
                        val indicatorField = DiscreteSeekBar::class.java.declaredFields.first { it.name == "mIndicator" }
                        indicatorField.isAccessible = true
                        val indicator = indicatorField.get(seekBar) as PopupIndicator
                        indicator.setColors(color, color)

                    }

                    override fun onStartTrackingTouch(seekBar: DiscreteSeekBar) {
                    }

                    override fun onStopTrackingTouch(seekBar: DiscreteSeekBar) {
                    }
                })*/

//                BubbleSeekBar(discrete1a.context).

//                DiscreteSeekBar(colorview1a.context).setIndicatorFormatter()
            }
        }

        override fun type1bSpinner(list: List<Type1ExtensionToString>, position: Int) {
//            type1bSpinner.onItemSelectedListener = null
            if (list.isEmpty()) {
                type1bSpinner.visibility = GONE
            } else {
                type1bSpinner.visibility = VISIBLE
                type1bSpinner.adapter = Type1SpinnerArrayAdapter(type1bSpinner.context, list)
                type1bSpinner.setSelection(position)
            }
        }

        override fun type1cSpinner(list: List<Type1ExtensionToString>, position: Int) {
//            type1cSpinner.onItemSelectedListener = null
            if (list.isEmpty()) {
                type1cSpinner.visibility = GONE
            } else {
                type1cSpinner.visibility = VISIBLE
                type1cSpinner.adapter = Type1SpinnerArrayAdapter(type1cSpinner.context, list)
                type1cSpinner.setSelection(position)
            }
        }

        override fun type2Spinner(list: List<Type2ExtensionToString>, position: Int) {
//            type2Spinner.onItemSelectedListener = null
            if (list.isEmpty()) {
                type2Spinner.visibility = GONE
            } else {
                type2Spinner.visibility = VISIBLE
                type2Spinner.list = list
                type2Spinner.setSelection(position)
            }
        }

        override fun setEnabled(enabled: Boolean) {
            appName.setTextColor(if (enabled) Color.GREEN else Color.RED)
        }

        override fun setCompiling(compiling: Boolean) {
            overlay.visibility = if (compiling) VISIBLE else GONE
        }

        override fun setInstalled(version1: String?, version2: String?) {
            if (version1 == null && version2 == null) {
                upToDate.setTextColor(Color.GREEN)
                upToDate.text = "Up to date"
            } else {
                upToDate.setTextColor(Color.RED)
                upToDate.text = "Update available: $version1 -> $version2"
            }
        }

        override fun reset() {
            upToDate.text = null
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
}
