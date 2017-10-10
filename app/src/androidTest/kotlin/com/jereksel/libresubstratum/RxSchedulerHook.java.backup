//https://gist.github.com/kaushikgopal/b680b69953cc5a5692581f69b8c9c65c
package com.jereksel.libresubstratum;

import android.os.AsyncTask;

import rx.Scheduler;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.functions.Func1;
import rx.plugins.RxJavaHooks;
import rx.plugins.RxJavaPlugins;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

public class RxSchedulerHook {

    private RxSchedulerHook() {
        // no instances
    }

    /**
     * this makes sure that when we run the tests all of RxJava
     * operates on a single thread (Scheduler.immediate)
     */
    public static void registerHooksForTesting() {
        RxJavaHooks.setOnComputationScheduler(new Func1<Scheduler, Scheduler>() {
            @Override
            public Scheduler call(Scheduler scheduler) {
                return Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR);
            }});
//        RxJavaPlugins.getInstance().registerSchedulersHook(new RxJavaTestSchedulerHook());
        //RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidTestSchedulerHook());
    }

    static class RxAndroidTestSchedulerHook
          extends RxAndroidSchedulersHook {

        // we use the async task scheduler because Espresso already knows how to handle this executor :D

        @Override
        public Scheduler getMainThreadScheduler() {
            return Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    static class RxJavaTestSchedulerHook
          extends RxJavaSchedulersHook {

        // we use the async task scheduler because Espresso already knows how to handle this executor :D

        @Override
        public Scheduler getComputationScheduler() {
            return Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        public Scheduler getIOScheduler() {
            return Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        @Override
        public Scheduler getNewThreadScheduler() {
            return Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}
