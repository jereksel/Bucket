package com.jereksel.libresubstratum.domain

import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.domain.DataHolder.THREAD_POOL_EXECUTOR
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratum.utils.ZipUtils.extractZip
import java.io.File
import java.util.concurrent.Future
import java.util.concurrent.FutureTask

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

        THREAD_POOL_EXECUTOR.execute {
            task.run()
        }

        return task
    }
}
