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

package com.jereksel.libresubstratum.activities.prioritiesdetail

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.InstalledOverlay

interface PrioritiesDetailContract {

    abstract class Presenter : MVPPresenter<View>() {
        abstract suspend fun getOverlays(targetId: String)
        abstract fun updateOverlays(overlays: List<InstalledOverlay>)
        abstract suspend fun updatePriorities(overlays: List<InstalledOverlay>)
        abstract fun openAppInSplit(targetId: String)
    }

    interface View : MVPView {
        fun setOverlays(overlays: List<InstalledOverlay>)
        fun showFab()
        fun hideFab()
        fun notifyPrioritiesChanged()
    }

}