package com.jereksel.libresubstratum.domain

import io.reactivex.Observable
import io.reactivex.Single

interface SubsDatabaseDownloader {
    fun getApps(): Single<List<SubstratumDatabaseTheme>>
    fun getClearThemes(): Single<List<SubstratumDatabaseTheme>>
    fun getDarkThemes(): Single<List<SubstratumDatabaseTheme>>
    fun getLightThemes(): Single<List<SubstratumDatabaseTheme>>
}