package com.jereksel.libresubstratumlib

import spock.lang.Ignore
import spock.lang.Specification

class ThemeReaderTest extends Specification {

    def resources = new File(getClass().classLoader.getResource("resource.json").path).parentFile

    def themeReader = new ThemeReader()

    def "simple theme pack test"() {
        when:
        def themeLocation = themeReader.readThemePack(File(resources, "VerySimpleTheme"))

        then:
        ["android", "com.android.settings", "com.android.systemui"] == themeLocation.themes.collect {it.application}.sort()
    }

    def "simple type2 test"() {
        when:
        def theme2 = themeReader.readType2Data(File(resources, "Type2Test", "overlays", "android"))

        then:
        ["Light", "Black", "Dark"] == theme2.extensions.collect { it.name }
    }


    def "type 1 test"() {
        when:
        def theme1 = themeReader.readType1Data(File(resources, "Type1Test", "overlays", "android"))

        then:
        ["a"] == theme1.collect { it.suffix }
    }

    def "type 1 test 2"() {
        when:
        def theme1 = themeReader.readType1Data(File(resources, "Type1Test", "overlays", "com.android.dialer"))

        then:
        ["a", "b"] == theme1.collect {it.suffix}
    }

//    fun File(init: File, vararg sub: String) : File {
//
//        return sub.fold(init) {
//            acc, elem ->
//            java.io.File(acc, elem)
//        }
//
//    }


    @Ignore
    def File(File init, String... sub) {
         sub.inject(init) { file, s -> new File(file, s) }
    }


}