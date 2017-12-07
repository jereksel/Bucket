/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    abstract class Presenter : MVPPresenter<View>() {
        //Activity
        abstract fun readTheme(appId: String)
        abstract fun getAppName(appId: String): String

        //ThemePackAdapter
        abstract fun getNumberOfThemes(): Int
        abstract fun setAdapterView(position: Int, view: ThemePackAdapterView)
        abstract fun setCheckbox(position: Int, checked: Boolean)

        abstract fun setType1a(position: Int, spinnerPosition: Int)
        abstract fun setType1b(position: Int, spinnerPosition: Int)
        abstract fun setType1c(position: Int, spinnerPosition: Int)
        abstract fun setType2(position: Int, spinnerPosition: Int)

        abstract fun setType3(type3Extension: Type3Extension)

        abstract fun compileAndRun(adapterPosition: Int)
        abstract fun openInSplit(adapterPosition: Int)
        abstract fun compileRunActivateSelected()
        abstract fun compileRunSelected()
        abstract fun selectAll()
        abstract fun deselectAll()
        abstract fun setClipboard(s: String)

        abstract fun restartSystemUI()
    }

}

