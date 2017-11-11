package com.jereksel.libresubstratum.data

import com.jereksel.changelogdialog.changelog

object Changelog {
    val changelog = changelog {
        betaVersion("0.2-BETA") {
            +"Changelog dialog"
            +"Spinners now show colors"
            +"Faster theme info loading"
        }
        version("0.1") {
            +"Initial release"
        }
    }
}