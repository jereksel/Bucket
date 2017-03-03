package com.jereksel.libresubstratum.domain

import android.support.annotation.VisibleForTesting
import com.jereksel.libresubstratum.data.Theme
import com.jereksel.libresubstratum.data.ThemePack
import com.jereksel.libresubstratum.data.Type3Data
import com.jereksel.libresubstratum.data.Type3Extension
import java.io.File

class ThemeReader {

    fun readThemePack(location: String) : ThemePack = readThemePack(File(location))

    fun readThemePack(location: File) : ThemePack {

        val overlaysLocation = File(location, "overlays")

        if (!overlaysLocation.exists()) {
            throw IllegalArgumentException("Overlays directory doesn't exists")
        }

        val themedPackages = overlaysLocation
                .listFiles()
                .filter { it.isDirectory }
                .map { Theme(it.name) }

        return ThemePack(themedPackages, Type3Data())
    }

    @VisibleForTesting
    fun readType3Data(location: File) : Type3Data {
        val dir = location.listFiles()[0]

        val type3File = File(dir, "type3")
        if (!type3File.exists()) {
            return Type3Data()
        }

        val firstType = Type3Extension(type3File.readText(), false)
        val rest = dir.listFiles().filter { it.name.startsWith("type3") }.map { it.name.removePrefix("type3_") }.map { Type3Extension(it, true) }.toTypedArray()

        return Type3Data(listOf(firstType, *rest))

    }
}