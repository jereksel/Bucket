package com.jereksel.libresubstratum.domain.usecases

import android.content.Context
import com.jereksel.libresubstratum.data.KeyPair
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

        val keyPair = keyFinder.getKey(appId)

        val transformer: (InputStream) -> InputStream

        if(keyPair == null) {
            transformer = { it }
        } else {
            transformer = {
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
                cipher.init(
                        Cipher.DECRYPT_MODE,
                        SecretKeySpec(keyPair.key.clone(), "AES"),
                        IvParameterSpec(keyPair.iv.clone())
                )
                CipherInputStream(it, cipher)
            }
        }

        val assets = context.packageManager.getResourcesForApplication(appId).assets
        return read(assets, transformer)
    }
}