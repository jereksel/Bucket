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

package com.jereksel.libresubstratum.activities.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.adapters.AboutLibrariesAdapter
import com.jereksel.libresubstratum.data.libraries.Author
import com.jereksel.libresubstratum.data.libraries.Library
import com.jereksel.libresubstratum.data.libraries.LicenseType.*
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class AboutActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.about)

        val maven = Library("Maven", APACHE2, Author("Maven contributors", "2014"))

        val kotlin = Library("Kotlin", APACHE2, Author("Jetbrains", "2015"))
        val support = Library("Android support library", APACHE2, Author("The Android Open Source Project", "2013"))

        val rxjava = Library("RxJava", APACHE2, Author("RxJava Contributors", "2016-present"))
        val rxkotlin = Library("RxKotlin", APACHE2, Author("RxJava and RxKotlin Contributors", "2016-present"))
        val rxandroid = Library("RxAndroid", APACHE2, Author("The RxAndroid authors", "2015"))

        val dagger = Library("Dagger2", APACHE2, Author("The Dagger Authors", "2012"))

        val kotterknife = Library("Kotterknife", APACHE2, Author("Jake Wharton", "2014"))

        val fab = Library("Clans' FloatingActionButton", APACHE2, Author("Dmytro Tarianyk", "2015"))

        val logback = Library("Logback Android", EPL, Author("QOS.ch", "1999-2014"))

        val msv = Library("MaterialShowcaseView", APACHE2, Author("Dean Wild", "2015"))

        val activityStarter = Library("Activity Starter", APACHE2, Author("Marcin Moskała", "2017"))

        val anko = Library("Anko", APACHE2, Author("Anko Contributors", "2017"))

        val result = Library("Result", MIT, Author("kittinunf", "2017"))

        val picasso = Library("Picasso", APACHE2, Author("Square, Inc.", "2013"))

        val guava = Library("Guava", APACHE2, Author("Guava contributors", "2010-present"))

        val zipUtils = Library("zt-zip", APACHE2, Author("ZeroTurnaround LLC.", "2012"))

        val room = Library("Room", APACHE2, Author("The Android Open Source Project", "2017"))

        val codec = Library("Apache Commons Codec", APACHE2, Author("The Apache Software Foundation", "2002-2017"))

        val crashlyticsAppender = Library("Crashlytics Appender", APACHE2, Author("Allogy Interactive", "2013"))

        val crash = Library("Custom Activity on Crash library", APACHE2, Author("Eduard Ereza", "2014-2017"))

        val elfio = Library("ELFIO", APACHE2, Author("Serge Lamikhov-Center", "2001-2011"))

        val changelogLib = Library("ChangeLog Library", APACHE2, Author("Gabriele Mariotti", "2013-2015"))

        val rxrelay = Library("RxRelay", APACHE2, Author("Netflix, Inc., Jake Wharton", "2014-2015"))

        val libraries = listOf(maven, kotlin, support, rxandroid, rxjava, rxkotlin, dagger,
                kotterknife, fab, logback, msv, activityStarter, anko, result, picasso, guava,
                zipUtils, room, codec, crashlyticsAppender, crash, elfio, changelogLib)
                .sortedBy { it.name }

        verticalLayout {
            recyclerView {
                adapter = AboutLibrariesAdapter(libraries)
                layoutManager = LinearLayoutManager(this@AboutActivity)
                itemAnimator = DefaultItemAnimator()
            }
        }
    }

}
