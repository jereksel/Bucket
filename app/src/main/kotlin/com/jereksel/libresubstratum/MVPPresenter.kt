package com.jereksel.libresubstratum

interface MVPPresenter<in T> where T: MVPView {
    fun setView(view: T)
    fun removeView()
}
