package com.jereksel.libresubstratumlib

class AndroidManifestGenerator(val testing: Boolean = false) {

    val metadataOverlayTarget = "Substratum_Target"
    val metadataOverlayParent = "Substratum_Parent"

    val metadataOverlayType1a = "Substratum_Type1a"
    val metadataOverlayType1b = "Substratum_Type1b"
    val metadataOverlayType1c = "Substratum_Type1c"
    val metadataOverlayType2 = "Substratum_Type2"
    val metadataOverlayType3 = "Substratum_Type3"

    fun generateManifest(theme: ThemeToCompile): String {

        val appId = theme.targetOverlayId
        val target = theme.fixedTargetApp
        val themeId = theme.targetThemeId

        val type1a = theme.getType("a")
        val type1b = theme.getType("b")
        val type1c = theme.getType("c")
        val type2 = theme.getType2()
        val type3 = theme.getType3()

        val xmlnsAndroid = if (testing) {
            "http://schemas.android.com/apk/lib/$appId"
        } else {
            "http://schemas.android.com/apk/res/android"
        }

        //TODO: DSL
        return """<?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="$xmlnsAndroid" package="$appId"
            android:versionCode="${theme.versionCode}" android:versionName="${theme.versionName}">

                <overlay android:priority="1" android:targetPackage="$target" />

                <application android:label="$appId" allowBackup="false" android:hasCode="false">
                    <meta-data android:name="$metadataOverlayTarget" android:value="$target" />
                    <meta-data android:name="$metadataOverlayParent" android:value="$themeId" />

                    <meta-data android:name="$metadataOverlayType1a" android:value="$type1a" />
                    <meta-data android:name="$metadataOverlayType1b" android:value="$type1b" />
                    <meta-data android:name="$metadataOverlayType1c" android:value="$type1c" />
                    <meta-data android:name="$metadataOverlayType2" android:value="$type2" />
                    <meta-data android:name="$metadataOverlayType3" android:value="$type3" />
                </application>
            </manifest>
        """
    }

    private fun ThemeToCompile.getType(type: String): String {

        val t = this.type1.find { it.suffix == type }?.extension ?: return ""

        if (t.default) {
            return ""
        } else {
            return t.name
        }

    }

    private fun ThemeToCompile.getType2(): String {

        val t = this.type2 ?: return ""

        if (t.default) {
            return ""
        } else {
            return t.name
        }

    }
    private fun ThemeToCompile.getType3(): String {

        val t = this.type3 ?: return ""

        if (t.default) {
            return ""
        } else {
            return t.name
        }
    }
}

