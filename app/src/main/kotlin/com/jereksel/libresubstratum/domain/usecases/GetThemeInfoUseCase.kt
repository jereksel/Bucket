package com.jereksel.libresubstratum.domain.usecases

import com.jereksel.libresubstratum.domain.IPackageManager
import com.jereksel.libresubstratum.domain.IThemeReader
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratum.extensions.getLogger
import com.jereksel.libresubstratumlib.ThemePack
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.codec.digest.MessageDigestAlgorithms.MD5
import org.zeroturnaround.zip.ZipUtil
import java.util.*

class GetThemeInfoUseCase(
        val packageManager: IPackageManager,
        val themePackDatabase: ThemePackDatabase,
        val themeReader: IThemeReader
): IGetThemeInfoUseCase {

    val log = getLogger()

    override fun getThemeInfo(appId: String): ThemePack {

        val appLocation = packageManager.getAppLocation(appId)

        val manifest = ZipUtil.unpackEntry(appLocation, "META-INF/MANIFEST.MF") ?: return themeReader.readThemePack(appId)

        val (md5, time) = timeOfExec { DigestUtils(MD5).digest(manifest) }

        log.debug("Time of MD5: {}", time)

        val oldChecksum = themePackDatabase.getThemePack(appId)?.second

        if (oldChecksum == null || !Arrays.equals(oldChecksum, md5)) {

            themePackDatabase.removeThemePack(appId)

            val pack = themeReader.readThemePack(appId)

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