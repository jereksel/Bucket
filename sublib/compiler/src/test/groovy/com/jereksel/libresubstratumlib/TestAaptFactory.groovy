package com.jereksel.libresubstratumlib

class TestAaptFactory {

    public static AAPT get() {
        if (System.getenv("CIRCLE_SHA1")) {
            //CircleCI
            println("Test runs on CircleCI")
            return new AAPT("/opt/android/sdk/build-tools/26.0.1/aapt")
        } else if (System.getenv("APPVEYOR")) {
            //AppVeyor
            println("Test runs on AppVeyor")
            return new AAPT("C:\\android\\build-tools\\26.0.1\\aapt.exe", true)
        } else {
            //Local
            println("Test runs on local PC")
            return new AAPT("${System.getProperty("user.home")}/Android/Sdk/build-tools/26.0.1/aapt", true)
        }

    }

}