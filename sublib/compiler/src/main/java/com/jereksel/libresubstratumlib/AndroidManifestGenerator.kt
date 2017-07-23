package com.jereksel.libresubstratumlib

object AndroidManifestGenerator {

    fun generateManifest(appId: String): String {

        return """
            <?xml version="1.0" encoding="utf-8"?>
                <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                    package="$appId">

            </manifest>
        """
    }

}