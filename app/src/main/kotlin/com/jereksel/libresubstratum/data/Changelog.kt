package com.jereksel.libresubstratum.data

import com.jereksel.changelogdialog.changelog

object Changelog {
    val changelog = changelog {
        betaVersion("0.2-BETA") {
            +"Fix FC in Swift Black (again)"
            +"Fix FC in installed overlays view"
            +"Searching in installed overlays view"
            +"Change toast text that shows when theme cannot be decrypted"
            +"Add screen about crash after FC"
            +"Fix text overlay in installed view"
            +"Fix FC in Swift Black"
            +"Changelog dialog"
            +"Spinners now show colors"
            +"Faster theme info loading"
        }
        version("0.1") {
            +"Initial release"
        }
    }
}