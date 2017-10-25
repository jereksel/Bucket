package com.jereksel.libresubstratum.domain.usecases

import android.content.Context
import com.jereksel.libresubstratum.data.KeyPair
import com.jereksel.libresubstratum.data.KeyPair.Companion.EMPTYKEY
import com.jereksel.libresubstratum.domain.IKeyFinder
import com.jereksel.libresubstratumlib.ThemePack
import com.jereksel.themereaderassetmanager.Reader.read
import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class GetThemeInfoUseCase(
        val context: Context,
        val keyFinder: IKeyFinder
): IGetThemeInfoUseCase {

    override fun getThemeInfo(appId: String): ThemePack {

        val keyPair = keyFinder.getKey(appId) ?: EMPTYKEY

        val assets = context.packageManager.getResourcesForApplication(appId).assets
        return read(assets, keyPair.getTransformer())
    }
}