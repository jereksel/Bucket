package com.jereksel.libresubstratum.activities.detailed2

sealed class DetailedAction {
    class InitialAction(val appId: String) : DetailedAction()
}