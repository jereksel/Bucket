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

package com.jereksel.libresubstratum.domain

import android.os.AsyncTask
import android.renderscript.ScriptGroup
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.extensions.getLogger
import java.io.File
import java.util.concurrent.Future
import java.util.concurrent.FutureTask
import com.jereksel.libresubstratum.utils.ZipUtils.extractZip
import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class BaseThemeExtractor : ThemeExtractor {

    val log = getLogger()

    override fun extract(file: File, dest: File, key: KeyPair?): Future<File> {

        val transform = (key ?: KeyPair.EMPTYKEY).getTransformer()

        val task = FutureTask {
            file.extractZip(dest, "assets", {
                log.debug("Extract progress: {}", it)
            }, transform)
            dest
        }

        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            task.run()
        }

        return task
    }
}
