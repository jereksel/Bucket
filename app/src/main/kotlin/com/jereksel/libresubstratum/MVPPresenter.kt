/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
