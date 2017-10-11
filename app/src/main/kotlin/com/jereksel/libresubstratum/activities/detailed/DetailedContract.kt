package com.jereksel.libresubstratum.activities.detailed

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.adapters.ThemePackAdapterView
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.libresubstratumlib.Type3Extension

interface DetailedContract {

    interface View : MVPView {
        fun addThemes(themePack: ThemePack)
        fun refreshHolder(position: Int)
        fun showToast(s: String)
        fun showSnackBar(message: String, buttonText: String, callback: () -> Unit)
        fun showCompilationProgress(size: Int)
        fun hideCompilationProgress()
        fun increaseDialogProgress()
        fun showError(errors: List<String>)
    }

    interface Presenter : MVPPresenter<View> {
        //Activity
        fun readTheme(appId: String)
        fun getAppName(appId: String): String
        fun setKey(key: KeyPair)

        //ThemePackAdapter
        fun getNumberOfThemes(): Int
        fun setAdapterView(position: Int, view: ThemePackAdapterView)
        fun setCheckbox(position: Int, checked: Boolean)

        fun setType1a(position:Int, spinnerPosition: Int)
        fun setType1b(position:Int, spinnerPosition: Int)
        fun setType1c(position:Int, spinnerPosition: Int)
        fun setType2(position:Int, spinnerPosition: Int)

        fun setType3(type3Extension: Type3Extension)

        fun compileAndRun(adapterPosition: Int)
        fun openInSplit(adapterPosition: Int)
        fun compileRunActivateSelected()
        fun compileRunSelected()
        fun selectAll()
        fun deselectAll()
        fun setClipboard(s: String)
    }

}

