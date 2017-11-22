package com.jereksel.libresubstratum.viewmodel

import com.jereksel.libresubstratum.BaseRobolectricTest
import com.jereksel.libresubstratum.activities.database.DatabaseViewModel
import com.jereksel.libresubstratum.domain.Pricing
import com.jereksel.libresubstratum.domain.SubsDatabaseDownloader
import com.jereksel.libresubstratum.domain.SubstratumDatabaseTheme
import com.jereksel.libresubstratum.presenters.PresenterTestUtils.initRxJava
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit

class DatabaseViewModelTest: BaseRobolectricTest() {

    lateinit var testScheduler: TestScheduler

    @Mock
    lateinit var subsDatabaseDownloader: SubsDatabaseDownloader

//    lateinit var databaseViewModel: DatabaseViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        testScheduler = TestScheduler()
        initRxJava()
//        RxJavaPlugins.setIoSchedulerHandler { testScheduler }
    }


    @Test
    fun ABC() {

        val list = listOf(SubstratumDatabaseTheme("", "", "", Pricing.PAID, listOf(), "", ""))

        whenever(subsDatabaseDownloader.getApps()).thenReturn(Single.just(list))

        val viewModel = DatabaseViewModel(subsDatabaseDownloader)

        var l: List<SubstratumDatabaseTheme>? = null

//            viewModel.getApps().value

//            viewModel.getApps().observeForever({l = it})

        testScheduler.triggerActions()
        testScheduler.advanceTimeBy(1, TimeUnit.MINUTES)

        l = viewModel.getApps().value

        assertThat(l).hasSize(1)
        assertThat(l).isEqualTo(list)
//            assertThat(l).containsExactly(SubstratumDatabaseTheme("", "", "", Pricing.PAID, listOf(), "", ""))

    }





}