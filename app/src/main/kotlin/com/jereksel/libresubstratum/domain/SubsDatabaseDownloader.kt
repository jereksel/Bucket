package com.jereksel.libresubstratum.domain

import io.reactivex.Observable
import io.reactivex.Single

interface SubsDatabaseDownloader {
    fun getApps(): Single<List<SubstratumDatabaseTheme>>
}