package com.jereksel.libresubstratum.extensions

import io.reactivex.disposables.Disposable

fun Disposable.safeDispose() = if (this.isDisposed) { dispose() } else {}
