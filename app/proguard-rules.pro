# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/andrzej/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontobfuscate
-optimizationpasses 5

# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-logback-android.pro
-keep class ch.qos.** { *; }
-keep class org.slf4j.** { *; }
-keepattributes *Annotation*
-dontwarn ch.qos.logback.core.net.*

-keep class com.allogy.android.** { *; }

# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-guava.pro
-keep class com.google.common.io.Resources {
    public static <methods>;
}
-keep class com.google.common.collect.Lists {
    public static ** reverse(**);
}
-keep class com.google.common.base.Charsets {
    public static <fields>;
}

-keep class com.google.common.base.Joiner {
    public static com.google.common.base.Joiner on(java.lang.String);
    public ** join(...);
}


# http://stackoverflow.com/questions/9120338/proguard-configuration-for-guava-with-obfuscation-and-optimization
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn sun.misc.Unsafe
-dontwarn afu.org.checkerframework.**
-dontwarn org.checkerframework.**

# Guava 19.0
-dontwarn java.lang.ClassValue
-dontwarn com.google.j2objc.annotations.Weak
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# okhttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-dontwarn kotlin.reflect.jvm.internal.**

-dontwarn com.google.errorprone.**

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class android.support.v7.widget.SearchView { *; }

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}