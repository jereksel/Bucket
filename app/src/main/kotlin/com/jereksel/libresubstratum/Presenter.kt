package com.jereksel.libresubstratum

interface Presenter<in T> where T: View {
    fun setView(view: T)
    fun removeView()
}
