package com.jereksel.libresubstratum.domain;

import android.support.annotation.Nullable;

public class KeyFinderNative {

    static {
        System.loadLibrary("nativelibresub");
    }

    //[key, iv]
    @Nullable
    public static native byte[][] getKeyAndIV(String location);
}
