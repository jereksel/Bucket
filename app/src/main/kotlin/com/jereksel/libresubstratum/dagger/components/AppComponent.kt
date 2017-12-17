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
import com.jereksel.libresubstratum.activities.detailed.DetailedView
import com.jereksel.libresubstratum.activities.installed.InstalledView
import com.jereksel.libresubstratum.activities.main.MainView
import com.jereksel.libresubstratum.activities.priorities.PrioritiesView
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailView
import com.jereksel.libresubstratum.dagger.modules.AppModule
import com.jereksel.libresubstratum.dagger.modules.GroupMetricsModule
import com.jereksel.libresubstratum.dagger.modules.MetricsModule
import com.jereksel.libresubstratum.receivers.UninstallReceiver
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, MetricsModule::class, GroupMetricsModule::class))
interface AppComponent {
    fun inject(view: MainView)
    fun inject(view: DetailedView)
    fun inject(view: InstalledView)
    fun inject(view: ErrorActivity)
    fun inject(view: PrioritiesView)
    fun inject(view: PrioritiesDetailView)
    fun inject(view: UninstallReceiver)
}
