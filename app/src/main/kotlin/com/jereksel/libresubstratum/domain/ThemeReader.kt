package com.jereksel.libresubstratum.domain

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.support.graphics.drawable.VectorDrawableCompat
import com.jereksel.libresubstratum.data.NavigationBarOverlay
import com.jereksel.libresubstratum.extensions.getLogger
import org.xmlpull.v1.XmlPullParserFactory
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class ThemeReader(val context: Context): IThemeReader {
    private val themeReaderImpl = com.jereksel.libresubstratumlib.ThemeReader()
    private val themeReaderExtractlessImpl = com.jereksel.libresubstratumlib.ThemeReaderExtractless()

    val log = getLogger()

    override fun readThemePack(location: File) = themeReaderExtractlessImpl.readThemePack(location)

    override fun isThemeEncrypted(location: File) = themeReaderImpl.checkIfEncrypted(location)

    override fun getNavigationBar(location: File, type: String): NavigationBarOverlay? {

        return ZipFile(location).use { zipFile ->

            val dir = "assets/overlays/com.android.systemui.navbars/type2_${type.trim()}/drawable-xxxhdpi-v4"


//            if (zipFile.getEntry("$dir/ic_sysbar_back.png") != null) {

                val left = zipFile.getEntry("$dir/ic_sysbar_back.png")?.getInputStream(zipFile)?.readBytes()
                val center = zipFile.getEntry("$dir/ic_sysbar_home.png")?.getInputStream(zipFile)?.readBytes()
                val right = zipFile.getEntry("$dir/ic_sysbar_recent.png")?.getInputStream(zipFile)?.readBytes()

                if (left == null || center == null || right == null) {
                    null
                } else {
                    NavigationBarOverlay(type, left, center, right)
                }

            // VectorDrawables cannot be created from external xmls (Resources.java)
            /**
                Caused by: java.lang.ClassCastException: android.util.XmlPullAttributes cannot be cast to android.content.res.XmlBlock$Parser
                at android.content.res.Resources.obtainAttributes(Resources.java:1758)
                at android.graphics.drawable.Drawable.obtainAttributes(Drawable.java:1433)
                at android.graphics.drawable.VectorDrawable.inflate(VectorDrawable.java:621)
                at android.graphics.drawable.DrawableInflater.inflateFromXml(DrawableInflater.java:130)
                at android.graphics.drawable.Drawable.createFromXmlInner(Drawable.java:1227)
                at android.graphics.drawable.Drawable.createFromXml(Drawable.java:1200)
                at android.graphics.drawable.Drawable.createFromXml(Drawable.java:1177)
                at com.jereksel.libresubstratum.domain.ThemeReader.getNavigationBar(ThemeReader.kt:50)
            */


//            } else {
//                val leftIs = zipFile.getEntry("assets/overlays/com.android.systemui.navbars/type2_${type.trim()}/drawable/ic_sysbar_back.xml")?.getInputStream(zipFile)
//                val content = leftIs?.bufferedReader()?.readText()
//                if (content != null) {
//                    val factory = XmlPullParserFactory.newInstance()
//                    factory.isNamespaceAware = true
//                    val xpp = factory.newPullParser()
//                    xpp.setInput(content.reader())
//                    val drawable = VectorDrawableCompat.createFromXml(context.resources, xpp)
//                    println(drawable)
//                }
////                log.debug(leftIs?.bufferedReader()?.readText())
////                val leftDw = Drawable.createFromStream(leftIs, null)
////                println(leftDw)
////                VectorDrawableCompat.createFromXml()
//
//                null
//            }
        }
    }

    private fun ZipEntry.getInputStream(zipFile: ZipFile) = zipFile.getInputStream(this)

}

