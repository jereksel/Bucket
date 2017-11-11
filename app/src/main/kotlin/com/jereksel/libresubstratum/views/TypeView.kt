package com.jereksel.libresubstratum.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.Spinner
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.adapters.Type1SpinnerArrayAdapter
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.extensions.getLogger
import org.jetbrains.anko.find

class TypeView : RelativeLayout, ITypeView {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    val log = getLogger()

    private val spinner: Spinner
    override fun getSpinner() = spinner
    private val seekbar: SeekBar
    override fun getSeekBar() = seekbar
    private val colorview: ColorView
    private var listener: ITypeView.TypeViewSelectionListener? = null

    init {
        val inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_typeview, this);

        spinner = find(R.id.spinner)
        seekbar = find(R.id.seekBar)
        colorview = find(R.id.colorview)

    }

    override fun onPositionChange(listener: ITypeView.TypeViewSelectionListener) {
        this.listener = listener
    }

    override fun setSelection(position: Int) {
        spinner.setSelection(position)
/*        seekbar.post {
            seekbar.progress = position
        }*/
    }

    override fun setType1(list: List<Type1ExtensionToString>) {
        spinner.adapter = Type1SpinnerArrayAdapter(context, list)
//        type1aSpinner.setSelection(position)
        val colors = list.map { it.type1.color }.map { if (it.isNotEmpty()) { it } else {"white"} }.map { Color.parseColor(it) }

        //ColorView is hidden for now. It was bad idea, I'll prepare something like this:
        //https://github.com/dmfs/color-picker

        colorview.visibility = View.GONE
        seekbar.visibility = View.GONE

/*
        if (colors.find { it != Color.parseColor("white") } == null || colors.size > 15) {
            //There are only whites or too much colors
            colorview.visibility = View.GONE
            seekbar.visibility = View.GONE
        } else {
            colorview.visibility = View.VISIBLE
            seekbar.visibility = View.VISIBLE
        }
*/

/*        colorview.colors = colors
        colorview.invalidate()*/

        val type1aSeekbar = seekbar
        val type1aSpinner = spinner
/*
        type1aSeekbar.post {

            type1aSeekbar.setOnSeekBarChangeListener(null)

            val width = type1aSeekbar.measuredWidth

            val margin = ((width.toFloat()/colors.size)/2).toInt()

            type1aSeekbar.setPadding(margin, 0, margin, 0)
            type1aSeekbar.progressDrawable = ColorDrawable(Color.TRANSPARENT)

            type1aSeekbar.max = colors.size - 1
            type1aSeekbar.progress = 0

            type1aSeekbar.invalidate()

            type1aSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    type1aSpinner.background = ColorDrawable(colors[progress])
                    type1aSpinner.setSelection(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }

            })
        }*/

        spinner.selectListener { position ->
            listener?.onPositionChange(position)
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
