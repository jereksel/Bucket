package com.jereksel.libresubstratum

interface Presenter<T> where T: View {
    fun setView(view: T)
    fun removeView()
}
