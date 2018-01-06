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

package com.jereksel.libresubstratum

import com.jereksel.libresubstratum.domain.OverlayService
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import com.jereksel.libresubstratum.utils.FutureUtils.toFuture

object Utils {

    fun initOS(overlayService: OverlayService) {
        whenever(overlayService.enableOverlay(any())).thenReturn(Unit.toFuture())
        whenever(overlayService.disableOverlay(any())).thenReturn(Unit.toFuture())
        whenever(overlayService.uninstallApk(any())).thenReturn(Unit.toFuture())
        whenever(overlayService.installApk(any())).thenReturn(Unit.toFuture())
        whenever(overlayService.enableExclusive(any())).thenReturn(Unit.toFuture())
    }

}

