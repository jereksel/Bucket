package com.jereksel.libresubstratum.domain.usecases

import android.content.Context
import com.jereksel.libresubstratum.data.KeyPair.Companion.EMPTYKEY
import com.jereksel.libresubstratum.domain.IKeyFinder
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.themereaderassetmanager.Reader.read
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms.SHA_1
import java.io.File
import java.util.*

class GetThemeInfoUseCase(
        val context: Context,
        val keyFinder: IKeyFinder,
        val themePackDatabase: ThemePackDatabase
): IGetThemeInfoUseCase {

    val log = getLogger()

    override fun getThemeInfo(appId: String): ThemePack {

        val keyPair = keyFinder.getKey(appId) ?: EMPTYKEY

        val (md5, time) = timeOfExec { DigestUtils(SHA_1).digest(File(context.packageManager.getApplicationInfo(appId, 0).sourceDir)) }

        log.debug("Time of MD5: {}", time)

        val oldChecksum = themePackDatabase.getChecksum(appId)

        if (oldChecksum == null || !Arrays.equals(oldChecksum, md5)) {

            themePackDatabase.removeThemePack(appId)

            val assets = context.packageManager.getResourcesForApplication(appId).assets
            val pack = read(assets, keyPair.getTransformer())

            themePackDatabase.addThemePack(appId, md5, pack)
            return pack
        } else {
            return themePackDatabase.getThemePack(appId)!!.first
        }

    }

    private inline fun <T> timeOfExec(f: () -> T): Pair<T,Long> {
        val t1 = System.currentTimeMillis()
        val t = f()
        val t2 = System.currentTimeMillis()
        return Pair(t, t2 - t1)
    }
}