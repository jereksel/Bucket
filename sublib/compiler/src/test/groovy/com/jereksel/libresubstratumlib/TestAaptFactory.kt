package com.jereksel.libresubstratumlib

object TestAaptFactory {

    @JvmStatic
    fun get(): AAPT {
        if (System.getenv("CIRCLE_SHA1").isNotEmpty()) {
            //CircleCI
            println("Test runs on CircleCI")
            return AAPT("${System.getenv("ANDROID_HOME")}/build-tools/26.0.2/aapt", true)
        } else if (System.getenv("APPVEYOR").isNotEmpty()) {
            //AppVeyor
            println("Test runs on AppVeyor")
            return AAPT("C:\\android\\build-tools\\26.0.2\\aapt.exe", true)
        } else {
            //Local
            println("Test runs on local PC")
            return AAPT("${System.getenv("ANDROID_HOME")}/build-tools/26.0.2/aapt", true)
        }

    }

}
