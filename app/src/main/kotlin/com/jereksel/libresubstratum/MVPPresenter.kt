package com.jereksel.libresubstratum

import io.reactivex.disposables.CompositeDisposable
import java.lang.ref.WeakReference

open class MVPPresenter<T> where T: MVPView {

    protected var view = WeakReference<T>(null)
    protected var compositeDisposable = CompositeDisposable()

    fun setView(view: T) {
        this.view = WeakReference(view)
    }

    fun removeView() {
        this.view = WeakReference<T>(null)
        compositeDisposable.dispose()
        compositeDisposable = CompositeDisposable()
    }
}
