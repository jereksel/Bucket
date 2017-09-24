package com.jereksel.libresubstratum.domain

interface IActivityProxy {

    fun openActivityInSplit(appId: String): Boolean

    fun showToast(text: String)

}