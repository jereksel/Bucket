/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.data

import com.jereksel.changelogdialog.changelog

object Changelog {
    val changelog = changelog {
        betaVersion("0.2-BETA") {
            +"Add option to change overlays priorities"
            +"Add support for template 11.1.0 (please report any issues)"
            +"Add support for type3-common (please report any issues)"
            +"Fix sorting in main activity"
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
