package com.jereksel.libresubstratumlib

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.zip.ZipFile

class ThemeReaderExtractless {

    val logger: Logger = LoggerFactory.getLogger(ThemeReaderExtractless::class.java)

    fun readThemePack(file: File): ThemePack {

        if (file.isDirectory) {
            throw IllegalArgumentException("File is a directory")
        }

        return ZipFile(file).use { zipFile ->

            val files = zipFile.entries().iterator().asSequence()
                    .map { it.name }
                    .filter { it.startsWith("assets/overlays") }
                    .map { it.removePrefix("assets/overlays/") }
                    .map { it.split("/", limit = 3) }
                    .filter { it.size >= 2 }
                    .map { it.subList(0, 2) }
                    .distinct()
                    .map { it[0] to it[1] }
                    .groupBy({ it.first }, { it.second })
//                    .map {
//                        it.key to it.value.map { it.second }
//                    }
//                    .toMap()
                    .toSortedMap()

            val first = files.entries.firstOrNull()

            val type3List = (first?.value ?: listOf())
                    .filter { it.startsWith("type3") }
                    .mapNotNull {
                        if (it == "type3") {
                            val entry = zipFile.getEntry("assets/overlays/${first?.key}/$it")
                            val name = zipFile.getInputStream(entry).bufferedReader().readText()
                            Type3Extension(name, true)
                        } else {
                            val name = it.removePrefix("type3_")
                            val entry = zipFile.getEntry("assets/overlays/${first?.key}/$name")
                            if (entry == null) {
                                //Type3 extensions are directories
                                Type3Extension(it.removePrefix("type3_"), false)
                            } else {
                                null
                            }
                        }
                    }
//                    .sortedBy { it.default.toString() + "_" + it.name }

//            val type3Default = type3.find { it.default }
//
//            val

            val type3 = if (type3List.isNotEmpty()) {

                logger.debug("Type3: {}", type3List)

                val defaultType3 = type3List.firstOrNull { it.default } ?: Type3Extension("Select preset", true)
                val otherType3 = (type3List - defaultType3).sortedBy { it.name }

                Type3Data(listOf(defaultType3) + otherType3)

            } else {
                null
            }

            val themes = files
                    .entries
                    .map { entry ->
                        val type2 = entry.value
                                .filter { it.startsWith("type2") }
                                .map {
                                    if (it == "type2") {
                                        val entry = zipFile.getEntry("assets/overlays/${entry.key}/$it")
                                        val name = zipFile.getInputStream(entry).bufferedReader().readText()
                                        Type2Extension(name, true)
                                    } else {
                                        Type2Extension(it.removePrefix("type2_"), false)
                                    }
                                }

                        logger.debug("Parsing overlay: {}", entry.key)

                        val type2Final = if(type2.isNotEmpty()) {

                            logger.debug("Type2: {}", type2)

                            val type2Default = type2.firstOrNull { it.default } ?: Type2Extension("Type2 Extension", true)
                            val type2Other = (type2 - type2Default).sortedBy { it.name }

                            Type2Data(listOf(type2Default) + type2Other)

                        } else {
                            null
                        }

                        val type1s = entry.value
                                .filter { it.startsWith("type1") }
                                .groupBy { it[5] }
                                .entries
                                .map { it.key.toString() to it.value }
                                .sortedBy { it.first }
                                .map {
                                    val id = it.first
                                    val files = it.second

                                    val type1extensions = files.map {
                                        if (it == "type1$id") {
                                            val entry = zipFile.getEntry("assets/overlays/${entry.key}/$it")
                                            val name = zipFile.getInputStream(entry).bufferedReader().readText()
                                            Type1Extension(name, true)
                                        } else {
                                            Type1Extension(it.removePrefix("type1${id}_").removeSuffix(".xml"), false)
                                        }
                                    }

                                    Type1Data(type1extensions, id)

                                }

                        Theme(entry.key, type1s, type2Final)
                    }

            ThemePack(themes, type3)
        }
    }

}