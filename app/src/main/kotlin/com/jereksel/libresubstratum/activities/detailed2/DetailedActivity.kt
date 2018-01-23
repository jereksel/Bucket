package com.jereksel.libresubstratum.activities.detailed2

import activitystarter.ActivityStarter
import activitystarter.Arg
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_LONG
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import com.hannesdorfmann.mosby3.mvi.MviActivity
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.data.Type3ExtensionToString
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.extensions.list
import com.jereksel.libresubstratum.extensions.selectListener
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.activity_detailed.*
import org.jetbrains.anko.find
import java.util.zip.GZIPOutputStream
import javax.inject.Inject

class DetailedActivity: MviActivity<DetailedView, DetailedPresenter>(), DetailedView {

    @Inject
    lateinit var detailedPresenter: DetailedPresenter

    @Arg
    lateinit var appId : String

    val uiAction = BehaviorSubject.create<DetailedAction>()

    val log = getLogger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed)
        ActivityStarter.fill(this)
        (application as App).getAppComponent(this).inject(this)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            //DefaultItemAnimator causes ripple on spinner change
            itemAnimator = null
            adapter = DetailedAdapter(listOf(), detailedPresenter)
        }
    }

    override fun createPresenter(): DetailedPresenter {
        detailedPresenter.appId = appId
        return detailedPresenter
    }

    override fun getActions(): Observable<DetailedAction> {
        return (recyclerView.adapter as DetailedAdapter).recyclerViewDetailedActions.mergeWith(uiAction)
    }

    override fun render(viewState: DetailedViewState) {
//        toast(viewState.toString())

        if (viewState.themePack != null) {
            (recyclerView.adapter as DetailedAdapter).update(viewState.themePack.themes)
        }

        if (viewState.compilationError != null) {
            val error = listOf(viewState.compilationError.toString())
            showError(error)
        }

        val type3 = viewState.themePack?.type3

        if (type3 != null && type3.data.isNotEmpty()) {
            spinner.visibility = VISIBLE
            spinner.list = type3.data.map { Type3ExtensionToString(it) }
            spinner.setSelection(type3.position)
            spinner.selectListener {
                uiAction.onNext(DetailedAction.ChangeType3SpinnerSelection(it))
            }
        } else {
            spinner.visibility = GONE
        }

        if (viewState.numberOfAllCompilations != 0) {
            progressBar.visibility = VISIBLE
            progressBar.progress = viewState.numberOfFinishedCompilations
            progressBar.max = viewState.numberOfAllCompilations
        } else {
            progressBar.visibility = GONE
        }

//        log.debug(viewState.toString())
//        textView.text = viewState.number.toString()
    }

    fun showError(errors: List<String>) {
        Snackbar.make(root, "Error occured during compilation", LENGTH_LONG)
                .setAction("Show error", {
                    val view = LayoutInflater.from(it.context).inflate(R.layout.dialog_compilationerror, null)
                    val textView = view.find<TextView>(R.id.errorTextView)
                    val errorText = errors.joinToString(separator = "\n")
                    textView.text = errorText

                    val builder = AlertDialog.Builder(it.context)
                    builder.setTitle("Error")
                    builder.setView(view)

//                    builder.setPositiveButton("Copy to clipboard", { _, _ ->
//                        presenter.setClipboard(errorText)
//                        toast("Copied to clipboard")
//                    })

                    builder.show()
                }).show()

    }

}

