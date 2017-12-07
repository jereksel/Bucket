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

package com.jereksel.libresubstratum.utils

import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

object ZipUtils {

    fun File.extractZip(dest: File, prefix: String = "", progressCallback: (Int) -> Unit = {},
                        streamTransform: (InputStream) -> InputStream = { it }) {
        if (dest.exists()) {
            dest.deleteRecursively()
        }
        dest.mkdirs()

        val length = ZipFile(this).size()

        FileInputStream(this).use { fis ->
            ZipInputStream(BufferedInputStream(fis)).use { zis ->
                zis.generateSequence().forEachIndexed { index, ze ->

                    val fileName = ze.name.removeSuffix(".enc")

                    progressCallback((index * 100) / length)

//                    Log.d("extractZip", fileName)

                    if (!fileName.startsWith(prefix)) {
                        return@forEachIndexed
                    }

                    if (ze.isDirectory) {
                        File(dest, fileName).mkdirs()
                        return@forEachIndexed
                    }

                    val destFile = File(dest, fileName)

                    destFile.parentFile.mkdirs()
                    destFile.createNewFile()

                    FileOutputStream(destFile).use { fout ->
                        streamTransform(zis).copyTo(fout)
                    }

                    if (Thread.interrupted()) {
                        return
                    }

                }
            }
        }
    }

    //We can't just use second function alone - we will close entry when there is no entry opened yet
    fun ZipInputStream.generateSequence() = generateSequence({ this.nextEntry }, { this.closeEntry(); this.nextEntry })

}