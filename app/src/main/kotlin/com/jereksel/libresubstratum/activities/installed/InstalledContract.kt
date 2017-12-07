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

package com.jereksel.libresubstratum.activities.installed

import com.jereksel.libresubstratum.MVPPresenter
import com.jereksel.libresubstratum.MVPView
import com.jereksel.libresubstratum.data.InstalledOverlay
import com.jereksel.libresubstratum.domain.OverlayInfo

interface InstalledContract {

    interface View : MVPView {
        fun addOverlays(overlays: List<InstalledOverlay>)
        fun updateOverlays(overlays: List<InstalledOverlay>)
        fun showSnackBar(message: String, buttonText: String, callback: () -> Unit)
        fun refreshRecyclerView()
    }

    abstract class Presenter : MVPPresenter<View>() {
        abstract fun getInstalledOverlays()
        abstract fun toggleOverlay(overlayId: String, enabled: Boolean)
        abstract fun getOverlayInfo(overlayId: String): OverlayInfo?
        abstract fun openActivity(appId: String): Boolean
        abstract fun uninstallSelected()
        abstract  fun disableSelected()
        abstract fun enableSelected()
        abstract fun selectAll()
        abstract fun deselectAll()
        abstract fun restartSystemUI()
        abstract fun setFilter(filter: String)

        //RecyclerView
        abstract fun setState(overlayId: String, isEnabled: Boolean)
        abstract fun getState(overlayId: String): Boolean
    }

}