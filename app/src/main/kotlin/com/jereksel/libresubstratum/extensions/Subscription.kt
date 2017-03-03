package com.jereksel.libresubstratum.extensions

import rx.Subscription

fun Subscription.safeUnsubscribe() = if (!isUnsubscribed) { unsubscribe() } else {}
