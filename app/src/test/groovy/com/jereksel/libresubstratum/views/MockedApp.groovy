package com.jereksel.libresubstratum.views

import android.app.Application
import com.jereksel.libresubstratum.App
import com.jereksel.libresubstratum.dagger.modules.AppModule
import com.jereksel.libresubstratum.domain.IPackageManager
import org.jetbrains.annotations.NotNull

import static org.mockito.Mockito.mock

class MockedApp extends App {

    IPackageManager mockedPackageManager = mock(IPackageManager);

    @Override
    protected AppModule getAppModule() {

        return new AppModule(this) {
            @Override
            IPackageManager providesPackageManager(@NotNull Application application) {
                return mockedPackageManager
            }
        }

    }
}

