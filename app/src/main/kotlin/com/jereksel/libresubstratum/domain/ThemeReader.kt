package com.jereksel.libresubstratum.domain

import android.support.annotation.VisibleForTesting
import com.jereksel.libresubstratum.data.*
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
    fun readType1Data(location: File) : List<Type1Data> {

        val typeMap = mutableMapOf<String, MutableList<Type1Extension>>()

        location.listFiles()
                .filter { it.name.startsWith("type1") }
                .forEach {
                    //TODO: Refactor this
                    val type = "${it.name[5]}"
                    if (it.name.length == 6) {
                        typeMap.getOrPut(type, { mutableListOf() }).add(Type1Extension(it.readText(), true))
                    } else {
                        val name = it.name.substring(7)
                        typeMap.getOrPut(type, { mutableListOf() }).add(Type1Extension(name, false))
                    }
                }

        return typeMap.map { Type1Data(it.value, it.key) }
    }

    @VisibleForTesting
    fun readType3Data(location: File) : Type3Data {
        val dir = location.listFiles()[0]

        val type3File = File(dir, "type3")
        if (!type3File.exists()) {
            return Type3Data()
        }

        val firstType = Type3Extension(type3File.readText(), true)
        val rest = dir.listFiles()
                .filter { it.isDirectory }
                .filter { it.name.startsWith("type3") }
                .map { it.name.removePrefix("type3_") }
                .map { Type3Extension(it, false) }
                .toTypedArray()

        return Type3Data(listOf(firstType, *rest))

    }
}