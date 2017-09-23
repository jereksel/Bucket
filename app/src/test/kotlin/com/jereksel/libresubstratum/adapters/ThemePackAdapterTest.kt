package com.jereksel.libresubstratum.adapters

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.View.VISIBLE
import com.jereksel.libresubstratum.BuildConfig
import com.jereksel.libresubstratum.MockedApp
import com.jereksel.libresubstratum.RecViewActivity
import com.jereksel.libresubstratum.ResettableLazy
import com.jereksel.libresubstratum.activities.detailed.DetailedContract.Presenter
import com.jereksel.libresubstratum.adapters.ThemePackAdapter.ViewHolder
import com.jereksel.libresubstratum.data.Type1ExtensionToString
import com.jereksel.libresubstratum.data.Type2ExtensionToString
import com.jereksel.libresubstratum.extensions.getAllStrings
import com.jereksel.libresubstratum.extensions.list
import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.reset
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.android.synthetic.main.activity_reconly.*
import org.assertj.android.recyclerview.v7.api.Assertions.assertThat
import org.assertj.android.api.Assertions.assertThat
import org.assertj.core.api.Assertions.*
import org.junit.Assert.*
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config

@Suppress("IllegalIdentifier")
@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class,
        application = MockedApp::class,
        sdk = intArrayOf(Build.VERSION_CODES.LOLLIPOP))
class ThemePackAdapterTest {

    lateinit var activityController: ActivityController<RecViewActivity>
    lateinit var activity: AppCompatActivity

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
        val viewHolder = recyclerView.getChildViewHolder(child) as ViewHolder

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
        val viewHolder = recyclerView.getChildViewHolder(child) as ViewHolder

        viewHolder.card.performClick()
        verify(presenter).setCheckbox(0, true)
        verify(presenter, never()).setCheckbox(0, false)
    }

    @Test
    fun `Long clicking on card invokes compileAndRun`() {
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
        val viewHolder = recyclerView.getChildViewHolder(child) as ViewHolder

        viewHolder.card.performLongClick()
        verify(presenter).compileAndRun(0)

    }

    @Test
    fun `1X Spinner selection is passes to presenter`() {
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
        val viewHolder = recyclerView.getChildViewHolder(child) as ViewHolder

        val spinners = listOf(
                viewHolder.type1aSpinner,
                viewHolder.type1bSpinner,
                viewHolder.type1cSpinner
        )

        val verifies = listOf(
                { verify(presenter).setType1a(0, 1) },
                { verify(presenter).setType1b(0, 1) },
                { verify(presenter).setType1c(0, 1) }
        )

        spinners.zip(verifies).forEach { (spinner, verify) ->

            val arr = listOf(
                    Type1ExtensionToString(Type1Extension("red", true)),
                    Type1ExtensionToString(Type1Extension("green", false))
            )

            spinner.visibility = VISIBLE
            spinner.list = arr
            spinner.setSelection(0)
            reset(presenter)
            spinner.setSelection(1)
            verify.invoke()
        }
    }


    @Test
    fun `2 Spinner selection is passes to presenter`() {
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
        val viewHolder = recyclerView.getChildViewHolder(child) as ViewHolder


        val arr = listOf(
                Type2ExtensionToString(Type2Extension("red", true)),
                Type2ExtensionToString(Type2Extension("green", false))
        )

        val spinner = viewHolder.type2Spinner

        spinner.visibility = View.VISIBLE
        spinner.list = arr
        spinner.setSelection(0)
        reset(presenter)
        spinner.setSelection(1)
        verify(presenter).setType2(0, 1)
    }

    private fun getViewHolder(): ViewHolder {
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
        return recyclerView.getChildViewHolder(child) as ViewHolder
    }

    @Test
    fun `setAppId sets appId text`() {
        val viewHolder = getViewHolder()
        viewHolder.setAppId("themeid")
        assertThat(viewHolder.appId).hasText("themeid")
        viewHolder.setAppId("themeid2")
        assertThat(viewHolder.appId).hasText("themeid2")
    }

    @Test
    fun `setAppName sets app text`() {
        val viewHolder = getViewHolder()
        viewHolder.setAppName("theme")
        assertThat(viewHolder.appName).hasText("theme")
        viewHolder.setAppName("theme 2")
        assertThat(viewHolder.appName).hasText("theme 2")
    }

    @Test
    fun `setCheckbox sets checkbox state`() {
        val viewHolder = getViewHolder()
        viewHolder.setCheckbox(true)
        assertThat(viewHolder.checkbox).isChecked
        viewHolder.setCheckbox(false)
        assertThat(viewHolder.checkbox).isNotChecked
    }

    @Test
    fun `Passing empty list to typeXSpinner should make is gone`() {
        val viewHolder = getViewHolder()

        val settera = { l: List<Type1ExtensionToString> -> viewHolder.type1aSpinner(l, 0) }
        val setterb = { l: List<Type1ExtensionToString> -> viewHolder.type1bSpinner(l, 0) }
        val setterc = { l: List<Type1ExtensionToString> -> viewHolder.type1cSpinner(l, 0) }

        listOf(
                viewHolder.type1aSpinner to settera,
                viewHolder.type1bSpinner to setterb,
                viewHolder.type1cSpinner to setterc
        ).forEach { (holder, setter) ->
            setter.invoke(listOf())
            assertThat(holder).isGone
        }
    }


    @Test
    fun `Passing list to type1XSpinner sets it as spinner's data`() {

        val viewHolder = getViewHolder()

        val settera = { l: List<Type1ExtensionToString>,i : Int -> viewHolder.type1aSpinner(l, i) }
        val setterb = { l: List<Type1ExtensionToString>,i: Int -> viewHolder.type1bSpinner(l, i) }
        val setterc = { l: List<Type1ExtensionToString>,i: Int -> viewHolder.type1cSpinner(l, i) }


        listOf(
                viewHolder.type1aSpinner to settera,
                viewHolder.type1bSpinner to setterb,
                viewHolder.type1cSpinner to setterc
        ).forEach { (holder, setter) ->

            val arr = listOf(
                    Type1ExtensionToString(Type1Extension("red", true)),
                    Type1ExtensionToString(Type1Extension("green", false))
            )

            setter.invoke(arr, 0)
            assertThat(holder.adapter.getAllStrings()).hasSameElementsAs(listOf("red", "green"))
            assertEquals(0, holder.selectedItemId)

            setter.invoke(arr, 1)
            assertThat(holder.adapter.getAllStrings()).hasSameElementsAs(listOf("red", "green"))
            assertEquals(1, holder.selectedItemId)

        }
    }

    @Test
    fun `Passing empty list to type2Spinner hides type2 spinner`() {
        val viewHolder = getViewHolder()
        viewHolder.type2Spinner(listOf(), 0)
        assertThat(viewHolder.type2Spinner).isGone
    }


    @Test
    fun `Passing list to type2Spinner sets it as spinner's data`() {

        val viewHolder = getViewHolder()
        val list = listOf(
                Type2ExtensionToString(Type2Extension("blue", true)),
                Type2ExtensionToString(Type2Extension("green", true))
        )

        val holder = viewHolder.type2Spinner

        viewHolder.type2Spinner(list, 0)
        assertThat(holder.adapter.getAllStrings()).hasSameElementsAs(listOf("blue", "green"))
        assertEquals(0, holder.selectedItemId)

        viewHolder.type2Spinner(list, 1)
        assertThat(holder.adapter.getAllStrings()).hasSameElementsAs(listOf("blue", "green"))
        assertEquals(1, holder.selectedItemId)

    }



}
