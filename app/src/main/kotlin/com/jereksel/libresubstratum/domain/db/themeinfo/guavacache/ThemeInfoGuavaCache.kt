package com.jereksel.libresubstratum.domain.db.themeinfo.guavacache

import com.google.common.cache.Cache
import com.google.common.cache.CacheBuilder
import com.jereksel.libresubstratum.domain.ThemePackDatabase
import com.jereksel.libresubstratumlib.ThemePack
import java.util.concurrent.TimeUnit

class ThemeInfoGuavaCache: ThemePackDatabase {

    val cache: Cache<String, Pair<ThemePack, ByteArray>> =
            CacheBuilder.newBuilder()
                    .expireAfterAccess(5, TimeUnit.MINUTES)
                    .maximumSize(20)
                    .build()

    override fun addThemePack(appId: String, checksum: ByteArray, themePack: ThemePack) {
        cache.put(appId, Pair(themePack, checksum))
    }

    override fun getThemePack(appId: String) = cache.getIfPresent(appId)

    override fun removeThemePack(appId: String) {
        cache.invalidate(appId)
    }

}
