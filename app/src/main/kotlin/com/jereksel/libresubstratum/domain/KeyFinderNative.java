package com.jereksel.libresubstratum.domain;

public class KeyFinderNative {

    static {
        System.loadLibrary("nativelibresub");
    }

    //[key, iv]
    public static native byte[][] getKeyAndIV(String location);
}
