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

package com.jereksel.libresubstratum.dagger.components

import com.jereksel.libresubstratum.activities.ErrorActivity
import com.jereksel.libresubstratum.activities.bottom.BottomActivity
import com.jereksel.libresubstratum.activities.detailed.DetailedView
import com.jereksel.libresubstratum.activities.detailed.DetailedActivity
import com.jereksel.libresubstratum.activities.installed.InstalledView
import com.jereksel.libresubstratum.activities.legal.LegalActivity
import com.jereksel.libresubstratum.activities.priorities.PrioritiesView
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailView
import com.jereksel.libresubstratum.activities.themelist.ThemeListView
import com.jereksel.libresubstratum.receivers.UninstallReceiver

interface BaseComponent {
    fun inject(view: LegalActivity)
    fun inject(view: ThemeListView)
    fun inject(view: DetailedView)
    fun inject(view: DetailedActivity)
    fun inject(view: InstalledView)
    fun inject(view: ErrorActivity)
    fun inject(view: PrioritiesView)
    fun inject(view: PrioritiesDetailView)
    fun inject(receiver: UninstallReceiver)
    fun inject(bottomActivity: BottomActivity)
}
