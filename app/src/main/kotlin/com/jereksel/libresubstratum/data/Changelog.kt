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
        betaVersion("0.3") {
            +"Rewrite activity with theme packs (now it properly support rotating and resizing)"
            +"Use Changelog Library instead of WebView"
            +"Fix compilation issues with more theme packs (Compound)"
            +"Fix PitchBlack compilation issues"
        }
        version("0.2.1") {
            +"Fix FC on 64 bit devices"
        }
        version("0.2") {
            +"Add option to change overlays priorities"
            +"Add support for template 11.1.0"
            +"Add support for type3-common"
            +"Searching in installed overlays view"
            +"Add screen about crash after FC"
            +"Changelog dialog"
            +"Spinners now show colors"
        }
        version("0.1") {
            +"Initial release"
        }
    }
}
