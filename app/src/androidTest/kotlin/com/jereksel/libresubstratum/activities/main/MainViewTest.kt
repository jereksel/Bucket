package com.jereksel.libresubstratum.activities.main

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.test.suitebuilder.annotation.LargeTest
import com.jereksel.libresubstratum.R
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.view.WindowManager
import com.jereksel.libresubstratum.activities.detailed.DetailedViewStarter
import org.apache.commons.lang3.reflect.FieldUtils

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainViewTest {

    @Suppress("RedundantVisibilityModifier")
    @Rule
    @JvmField
    public var mActivityTestRule = IntentsTestRule(MainView::class.java)

    @Before
    fun setUp() {
//        RxSchedulerHook.registerHooksForTesting()
        val activity = mActivityTestRule.activity
        val wakeUpDevice = Runnable {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        activity.runOnUiThread(wakeUpDevice)
    }

    @Test
    fun mainViewTest() {
        val recyclerView = onView(
                allOf(withId(R.id.recyclerView),
                        withParent(allOf(withId(R.id.swiperefresh),
                                withParent(withId(R.id.activity_main)))),
                        isDisplayed()))

        recyclerView.perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

//        onView(withText("Extracting theme")).check(matches(isDisplayed()));

//        intended(hasComponent(ComponentName("com.jereksel.libresubstratum", ".activities.detailed.detailedView_")))
//        intended(hasComponent(DetailedView_::javaClass.name))
//        Intents.intended(IntentMatchers.hasComponent(ComponentName(getTargetContext(), DetailedView_::class.java)))
//        Intents.intended(IntentMatchers.hasExtra("appId", notNull<String>()))
        val keyName = FieldUtils.readStaticField(DetailedViewStarter::class.java, "APP_ID_KEY", true) as String
        Intents.intended(IntentMatchers.toPackage("com.jereksel.libresubstratum"))
        Intents.intended(IntentMatchers.hasExtraWithKey(keyName))

    }
}
