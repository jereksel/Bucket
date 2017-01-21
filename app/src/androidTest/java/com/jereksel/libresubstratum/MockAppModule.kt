package com.jereksel.libresubstratum

import com.jereksel.libresubstratum.domain.IPackageManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MockAppModule {

    @Provides
    @Singleton
    internal fun providesPackageManager(): IPackageManager? {
//        return Mockito.mock(IPackageManager::class.java)
        return null
    }
}
