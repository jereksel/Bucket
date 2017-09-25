package com.jereksel.libresubstratum.activities.navigationbar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jereksel.libresubstratum.R
import com.jereksel.libresubstratum.activities.navigationbar.NavigationBarContract.*
import javax.inject.Inject

class NavigationBarView: AppCompatActivity(), View {

    @Inject lateinit var presenter: Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigationbar)
    }

}