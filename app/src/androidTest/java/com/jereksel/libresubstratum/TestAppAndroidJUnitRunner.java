package com.jereksel.libresubstratum;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnitRunner;

public class TestAppAndroidJUnitRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(@NonNull ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, TestApp.class.getName(), context);
    }
}
