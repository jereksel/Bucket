package com.jereksel.libresubstratum.activities.about

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jereksel.libresubstratum.data.libraries.Author
import com.jereksel.libresubstratum.data.libraries.Library
import com.jereksel.libresubstratum.data.libraries.LicenseType.*
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView

class AboutActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val anko = Library("Anko", APACHE2)

        val kotlin = Library("Kotlin", APACHE2)
        val support = Library("Android support library", APACHE2, Author("The Android Open Source Project", "2013"))

        val rxjava = Library("RxJava", APACHE2, Author("RxJava Contributors", "2016-present"))
        val rxkotlin = Library("RxKotlin", APACHE2, Author("RxJava and RxKotlin Contributors", "2016-present"))
        val rxandroid = Library("RxAndroid", APACHE2, Author("The RxAndroid authors", "2015"))


        verticalLayout {
            recyclerView {
                adapter
            }
        }
    }
}