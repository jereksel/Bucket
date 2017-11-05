package com.jereksel.libresubstratumlib.assetmanager

typealias AAPT  = com.jereksel.libresubstratumlib.assetmanager.AaptCompiler

object TestAaptFactory {

    fun get(): AAPT {
        if (!System.getenv("CIRCLE_SHA1").isNullOrBlank()) {
            //CircleCI
            println("Test runs on CircleCI")
            return AAPT("${System.getenv("ANDROID_HOME")}/build-tools/26.0.2/aapt", true)
        } else if (!System.getenv("APPVEYOR").isNullOrBlank()) {
            //AppVeyor
            println("Test runs on AppVeyor")
            return AAPT("C:\\android\\build-tools\\26.0.2\\aapt.exe", true)
        } else {
            //Local
            println("Test runs on local PC")
            return AAPT("${System.getenv("ANDROID_HOME")}/build-tools/26.0.2/aapt", true)
        }

    }


    fun get2(): com.jereksel.libresubstratumlib.AAPT {
        if (!System.getenv("CIRCLE_SHA1").isNullOrBlank()) {
            //CircleCI
            println("Test runs on CircleCI")
            return com.jereksel.libresubstratumlib.AAPT("${System.getenv("ANDROID_HOME")}/build-tools/26.0.2/aapt", true)
        } else if (!System.getenv("APPVEYOR").isNullOrBlank()) {
            //AppVeyor
            println("Test runs on AppVeyor")
            return com.jereksel.libresubstratumlib.AAPT("C:\\android\\build-tools\\26.0.2\\aapt.exe", true)
        } else {
            //Local
            println("Test runs on local PC")
            return com.jereksel.libresubstratumlib.AAPT("${System.getenv("ANDROID_HOME")}/build-tools/26.0.2/aapt", true)
        }

    }


}
