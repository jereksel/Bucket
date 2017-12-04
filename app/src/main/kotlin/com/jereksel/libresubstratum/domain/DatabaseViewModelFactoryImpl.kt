package com.jereksel.libresubstratum.domain

import android.arch.lifecycle.ViewModel
import com.jereksel.libresubstratum.activities.database.DatabaseViewModel

class DatabaseViewModelFactoryImpl(
        val downloader: SubsDatabaseDownloader
): DatabaseViewModelFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = DatabaseViewModel(downloader) as T

}