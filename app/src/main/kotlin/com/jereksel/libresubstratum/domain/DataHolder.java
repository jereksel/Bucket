package com.jereksel.libresubstratum.domain;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;

import java.util.concurrent.Executor;

public class DataHolder {
    public static final int FLAG_ACTIVITY_LAUNCH_ADJACENT = Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT;
    public static final int N = Build.VERSION_CODES.N;
    public static final int N_MR1 = Build.VERSION_CODES.N_MR1;
    public static final Executor THREAD_POOL_EXECUTOR = AsyncTask.THREAD_POOL_EXECUTOR;
}
