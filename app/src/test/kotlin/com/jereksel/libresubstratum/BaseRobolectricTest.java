package com.jereksel.libresubstratum;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.ArraySet;
import android.view.View;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

import java.util.ArrayList;

import static org.robolectric.Robolectric.flushBackgroundThreadScheduler;
import static org.robolectric.Robolectric.flushForegroundThreadScheduler;
import static org.robolectric.shadows.ShadowApplication.runBackgroundTasks;
import static org.robolectric.shadows.ShadowLooper.runUiThreadTasksIncludingDelayedTasks;

/**
 * Base test class to cleanup Robolectric window manager memory leak and finish background threads.
 * Source: https://github.com/mapzen/lost/blob/229afcaad0bf9ae03370b7431f7428ce6823c2d6/lost/src/test/java/com/mapzen/android/lost/BaseRobolectricTest.java
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = MockedApp.class,
        shadows = ShadowSnackbar.class,
        sdk = Build.VERSION_CODES.N)
public class BaseRobolectricTest {

    // https://github.com/robolectric/robolectric/issues/2068
    @After @SuppressLint("NewApi") public void resetWindowManager() {
        try {
            Class clazz = ReflectionHelpers.loadClass(getClass().getClassLoader(),
                    "android.view.WindowManagerGlobal");
            Object instance = ReflectionHelpers.callStaticMethod(clazz, "getInstance");

            // We essentially duplicate what's in {@link WindowManagerGlobal#closeAll} with what's below.
            // The closeAll method has a bit of a bug where it's iterating through the "roots" but
            // bases the number of objects to iterate through by the number of "views." This can result in
            // an {@link java.lang.IndexOutOfBoundsException} being thrown.
            Object lock = ReflectionHelpers.getField(instance, "mLock");

            ArrayList<Object> roots = ReflectionHelpers.getField(instance, "mRoots");
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (lock) {
                for (int i = 0; i < roots.size(); i++) {
                    ReflectionHelpers.callInstanceMethod(instance, "removeViewLocked",
                            ReflectionHelpers.ClassParameter.from(int.class, i),
                            ReflectionHelpers.ClassParameter.from(boolean.class, false));
                }
            }

            // Views will still be held by this array. We need to clear it out to ensure
            // everything is released.
            ArraySet<View> dyingViews = ReflectionHelpers.getField(instance, "mDyingViews");
            dyingViews.clear();
        } catch (Exception e) {
            // Catch ClassNotFoundException for API levels where WindowManagerGlobal doesn't exits.
        }
    }

    @Test
    public void emptyTest() {

    }

    @After public void finishThreads() {
        runBackgroundTasks();
        flushForegroundThreadScheduler();
        flushBackgroundThreadScheduler();
        runUiThreadTasksIncludingDelayedTasks();
        resetBackgroundThread();
    }

    // https://github.com/robolectric/robolectric/pull/1741
    private void resetBackgroundThread() {
        try {
            final Class<?> btclass = Class.forName("com.android.internal.os.BackgroundThread");
            final Object backgroundThreadSingleton = ReflectionHelpers.getStaticField(btclass,
                    "sInstance");
            if (backgroundThreadSingleton != null) {
                btclass.getMethod("quit").invoke(backgroundThreadSingleton);
                ReflectionHelpers.setStaticField(btclass, "sInstance", null);
                ReflectionHelpers.setStaticField(btclass, "sHandler", null);
            }
        } catch (Exception e) {
            // Catch ClassNotFoundException for API levels where BackgroundThread doesn't exits.
        }
    }
}