package com.jereksel.libresubstratum.activities.detailed2

import arrow.optics.Optional
import arrow.syntax.either.left
import arrow.syntax.either.right

fun <A> listElementPositionOptional(position: Int): Optional<List<A>, A> = Optional(
        getOrModify = { l -> l.getOrNull(position)?.right() ?: l.left() },
        set = { e -> { l -> l.mapIndexed { index: Int, value: A -> if (index == position) e else value } } }
)

fun detailedViewStateThemePackOptional(): Optional<DetailedViewState, DetailedViewState.ThemePack> = Optional(
        getOrModify = { s -> s.themePack?.right() ?: s.left()},
        set = { a -> { b -> b.copy(themePack = a) }}
)

fun themePackType3Optional(): Optional<DetailedViewState.ThemePack, DetailedViewState.Type3> = Optional(
        getOrModify = { s -> s.type3?.right() ?: s.left()},
        set = { a -> { b -> b.copy(type3 = a) }}
)

fun themeType1aOptional(): Optional<DetailedViewState.Theme, DetailedViewState.Type1> = Optional(
        getOrModify = { s -> s.type1a?.right() ?: s.left()},
        set = { a -> { b -> b.copy(type1a = a) }}
)

fun themeType1bOptional(): Optional<DetailedViewState.Theme, DetailedViewState.Type1> = Optional(
        getOrModify = { s -> s.type1b?.right() ?: s.left()},
        set = { a -> { b -> b.copy(type1b = a) }}
)

fun themeType1cOptional(): Optional<DetailedViewState.Theme, DetailedViewState.Type1> = Optional(
        getOrModify = { s -> s.type1c?.right() ?: s.left()},
        set = { a -> { b -> b.copy(type1c = a) }}
)

fun themeType2Optional(): Optional<DetailedViewState.Theme, DetailedViewState.Type2> = Optional(
        getOrModify = { s -> s.type2?.right() ?: s.left()},
        set = { a -> { b -> b.copy(type2 = a) }}
)

fun <T> listElementOptional(i: Int): Optional<List<T>, T> = listElementPositionOptional(i)
