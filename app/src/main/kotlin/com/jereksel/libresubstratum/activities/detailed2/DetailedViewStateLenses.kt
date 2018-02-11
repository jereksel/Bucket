package com.jereksel.libresubstratum.activities.detailed2

import arrow.optics.Optional
import arrow.syntax.either.left
import arrow.syntax.either.right

fun <A> listElementPositionOptional(position: Int): Optional<List<A>, A> = Optional(
        getOrModify = { l -> l.getOrNull(position)?.right() ?: l.left() },
        set = { e -> { l -> l.mapIndexed { index: Int, value: A -> if (index == position) e else value } } }
)

fun <T> listElementOptional(i: Int): Optional<List<T>, T> = listElementPositionOptional(i)
