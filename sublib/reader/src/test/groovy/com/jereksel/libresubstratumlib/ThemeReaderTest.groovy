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

    def "type 1 empty theme test"() {
        when:
        def theme1 = themeReader.readType1Data(File(resources, "VerySimpleTheme", "overlays", "android"))

        then:
        theme1.empty
    }

    def "type 1 android test"() {
        when:
        def theme1 = themeReader.readType1Data(File(resources, "Type1Test", "overlays", "android"))

        then:
        ["a"] == theme1.collect { it.suffix }
    }

    def "type 1 com.android.dialer test"() {
        when:
        def theme1 = themeReader.readType1Data(File(resources, "Type1Test", "overlays", "com.android.dialer"))

        then:
        ["a", "b"] == theme1.collect {it.suffix}
    }

    def "get type1a android types test"() {
        when:
        def theme1 = themeReader.readType1Data(File(resources, "Type1Test", "overlays", "android"))
        def type1a = theme1[0].extension

        then:
        ["Green", "Initial color", "Red"] == type1a.collect {it.name}.sort()
    }

    def "simple type2 test"() {
        when:
        def theme2 = themeReader.readType2Data(File(resources, "Type2Test", "overlays", "android"))

        then:
        ["Light", "Black", "Dark"] == theme2.extensions.collect { it.name }
    }

    def "simple type2 empty theme test"() {
        when:
        def theme2 = themeReader.readType2Data(File(resources, "VerySimpleTheme", "overlays", "android"))

        then:
        theme2 == null
    }

    def "simple type3 test"() {
        when:
        def theme3 = themeReader.readType3Data(File(resources, "Type3Test", "overlays"))

        then:
        ["Light", "Black", "Dark"] == theme3.extensions.collect { it.name }
    }

    def "simple type3 empty theme test"() {
        when:
        def theme3 = themeReader.readType3Data(File(resources, "VerySimpleTheme", "overlays"))

        then:
        theme3 == null
    }

    @Ignore
    def File(File init, String... sub) {
        sub.inject(init) { file, s -> new File(file, s) }
    }
}