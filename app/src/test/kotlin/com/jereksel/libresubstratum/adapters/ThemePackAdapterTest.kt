package com.jereksel.libresubstratum.adapters

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.jereksel.libresubstratum.BuildConfig
import com.jereksel.libresubstratum.MockedApp
import com.jereksel.libresubstratum.RecViewActivity
import com.jereksel.libresubstratum.ResettableLazy
import com.jereksel.libresubstratum.activities.detailed.DetailedContract
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.domain.IActivityProxy
import io.kotlintest.mock.mock
import kotlinx.android.synthetic.main.activity_reconly.*
import org.assertj.android.api.Assertions.assertThat
import org.assertj.android.recyclerview.v7.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import com.jereksel.libresubstratum.extensions.*
import com.jereksel.libresubstratumlib.*
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
//import org.assertj.core.api.Assertions.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class ThemePackAdapterTest {

    lateinit var activityController : ActivityController<RecViewActivity>
    lateinit var activity : AppCompatActivity

    var activityCasted by ResettableLazy { activity as AppCompatActivity? }
    var recyclerView by ResettableLazy { activityCasted!!.recyclerView }

    @Mock
    lateinit var presenter: Presenter

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        activityController = Robolectric.buildActivity(RecViewActivity::class.java).create()
        activity = activityController.get()
    }

    @After
    fun cleanup() {
//        activityController.destroy()
        activityCasted = null
        recyclerView = null
    }

    @Test
    fun `Checkbox invokes should be passed to presenter`() {
        whenever(presenter.getNumberOfThemes()).thenReturn(1)
        val adapter_ = ThemePackAdapter(presenter)
        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
            measure(0, 0)
            layout(0, 0, 100, 10000)
        }
        assertThat(adapter_).hasItemCount(1)
        val child = recyclerView.layoutManager.findViewByPosition(0)
        val viewHolder = recyclerView.getChildViewHolder(child) as ThemePackAdapter.ViewHolder

        viewHolder.checkbox.performClick()
        verify(presenter).setCheckbox(0, true)
        verify(presenter, never()).setCheckbox(0, false)

        reset(presenter)

        viewHolder.checkbox.performClick()
        verify(presenter).setCheckbox(0, false)
        verify(presenter, never()).setCheckbox(0, true)
    }

    @Test
    fun `Clicking on card itself should toggle checkbox`() {
        whenever(presenter.getNumberOfThemes()).thenReturn(1)
        val adapter_ = ThemePackAdapter(presenter)
        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
            measure(0, 0)
            layout(0, 0, 100, 10000)
        }
        assertThat(adapter_).hasItemCount(1)
        val child = recyclerView.layoutManager.findViewByPosition(0)
        val viewHolder = recyclerView.getChildViewHolder(child) as ThemePackAdapter.ViewHolder

        viewHolder.card.performClick()
        verify(presenter).setCheckbox(0, true)
        verify(presenter, never()).setCheckbox(0, false)
    }

/*
    @Test
    fun `Theme without extensions shouldn't have spinners`() {
        val theme = ThemePack(listOf(Theme("app1")))
        val adapter_ = ThemePackAdapter(theme, mock())
        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
            measure(0, 0)
            layout(0, 0, 100, 10000)
        }
        assertThat(adapter_).hasItemCount(1)
        val child = recyclerView.layoutManager.findViewByPosition(0)
        val viewHolder = recyclerView.getChildViewHolder(child) as ThemePackAdapter.ViewHolder
        viewHolder.type1Spinners.forEach { assertThat(it).isGone }
        assertThat(viewHolder.type2Spinner).isGone
    }

    @Test
    fun `Theme with only type2 extension have t2 spinner visible and t1 spinners gone`() {
        val type2s = listOf("a", "b", "c")
        val theme = ThemePack(listOf(Theme("app1", type2 = Type2Data(type2s.map { Type2Extension(it, false) }))))
        val adapter_ = ThemePackAdapter(theme, mock())
        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
            measure(0, 0)
            layout(0, 0, 100, 10000)
        }
        assertThat(adapter_).hasItemCount(1)
        val child = recyclerView.layoutManager.findViewByPosition(0)
        val viewHolder = recyclerView.getChildViewHolder(child) as ThemePackAdapter.ViewHolder
        viewHolder.type1Spinners.forEach { assertThat(it).isGone }
        assertThat(viewHolder.type2Spinner).isVisible
        assertThat(viewHolder.type2Spinner.getAllStrings()).isEqualTo(type2s)
    }

    @Test
    fun `Theme with only t1 extension should have t1 spinners visible and t2 spinner gone`() {
        val type1s = listOf("a", "b", "c")
        val theme = ThemePack(listOf(Theme("app1", type1 = listOf(Type1Data(type1s.map { Type1Extension(it, false) }, "a")))))
        val adapter_ = ThemePackAdapter(theme, mock())
        recyclerView.run {
            layoutManager = LinearLayoutManager(context)
            itemAnimator = DefaultItemAnimator()
            adapter = adapter_
            measure(0, 0)
            layout(0, 0, 100, 10000)
        }
        assertThat(adapter_).hasItemCount(1)
        val child = recyclerView.layoutManager.findViewByPosition(0)
        val viewHolder = recyclerView.getChildViewHolder(child) as ThemePackAdapter.ViewHolder
//        viewHolder.type1Spinners.forEach { assertThat(it).isGone }
        assertThat(viewHolder.type1aSpinner).isVisible
        assertThat(viewHolder.type1bSpinner).isGone
        assertThat(viewHolder.type1cSpinner).isGone
        assertThat(viewHolder.type2Spinner).isGone
        assertThat(viewHolder.type1aSpinner.getAllStrings()).isEqualTo(type1s)
    }*/
}
