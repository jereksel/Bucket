package com.jereksel.libresubstratumlib

class TestAaptFactory {

    public static AAPT get() {
        if (System.getenv("CIRCLE_SHA1")) {
            //CircleCI
            println("Test runs on CircleCI")
            return new AAPT("${System.getenv("ANDROID_HOME")}/build-tools/26.0.2/aapt", true)
        } else if (System.getenv("APPVEYOR")) {
            //AppVeyor
            println("Test runs on AppVeyor")
            return new AAPT("C:\\android\\build-tools\\26.0.2\\aapt.exe", true)
        } else {
            //Local
            println("Test runs on local PC")
            return new AAPT("${System.getenv("ANDROID_HOME")}/build-tools/26.0.2/aapt", true)
        }

    }

}
