package com.jereksel.libresubstratum.activities.detailed2

import io.reactivex.functions.BiFunction

object DetailedReducer: BiFunction<DetailedViewState, DetailedResult, DetailedViewState> {

    override fun apply(t1: DetailedViewState, t2: DetailedResult): DetailedViewState {
        return when(t2) {
            is DetailedResult.ListLoaded -> t1.copy(type3 = DetailedViewState.Type3(t2.type3, 0))
        }
    }

}