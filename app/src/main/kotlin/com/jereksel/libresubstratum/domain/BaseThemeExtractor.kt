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
