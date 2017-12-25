package com.jereksel.libresubstratum

import android.content.Context
import com.jereksel.libresubstratum.activities.detailed.DetailedContract
import com.jereksel.libresubstratum.activities.installed.InstalledContract
import com.jereksel.libresubstratum.activities.main.MainContract
import com.jereksel.libresubstratum.activities.priorities.PrioritiesContract
import com.jereksel.libresubstratum.activities.prioritiesdetail.PrioritiesDetailContract
import com.jereksel.libresubstratum.dagger.components.BaseComponent
import com.jereksel.libresubstratum.domain.Metrics
import dagger.Component
import dagger.Module
import dagger.Provides
import io.kotlintest.mock.mock
import org.mockito.Mockito
import javax.inject.Named
import javax.inject.Singleton

class MockedApp : App() {

    companion object {
        @JvmStatic
        val mockedMainPresenter: MainContract.Presenter = Mockito.mock(MainContract.Presenter::class.java)
        @JvmStatic
        val mockedDetailedPresenter: DetailedContract.Presenter = Mockito.mock(DetailedContract.Presenter::class.java)
        @JvmStatic
        val mockedInstalledPresenter: InstalledContract.Presenter = Mockito.mock(InstalledContract.Presenter::class.java)
        @JvmStatic
        val mockedPrioritiesPresenter: PrioritiesContract.Presenter = Mockito.mock(PrioritiesContract.Presenter::class.java)
        @JvmStatic
        val mockedPrioritiesDetailPresenter: PrioritiesDetailContract.Presenter = Mockito.mock(PrioritiesDetailContract.Presenter::class.java)

        @Module
        class TestModule {

            @Provides
            fun main(): MainContract.Presenter = mockedMainPresenter
            @Provides
            fun detailed(): DetailedContract.Presenter = mockedDetailedPresenter
            @Provides
            fun installed(): InstalledContract.Presenter = mockedInstalledPresenter
            @Provides
            fun prorities(): PrioritiesContract.Presenter = mockedPrioritiesPresenter
            @Provides
            fun proritiesdetail(): PrioritiesDetailContract.Presenter = mockedPrioritiesDetailPresenter
            @Provides
            @Named("persistent")
            fun metrics(): Metrics = mock()

        }

        @Singleton
        @Component(modules = [TestModule::class])
        interface TestComponent: BaseComponent

    }

    override fun getAppComponent(context: Context): BaseComponent {
        return DaggerMockedApp_Companion_TestComponent.create()
    }

}
