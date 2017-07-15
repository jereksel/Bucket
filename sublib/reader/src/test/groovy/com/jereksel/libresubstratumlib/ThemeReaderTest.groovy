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

    def "simple type2 test"() {
        when:
        def theme2 = themeReader.readType2Data(File(resources, "type2test", "overlays", "android"))

        then:
        ["Light", "Black", "Dark"] == theme2.extensions.collect { it.name }
    }

    def "simple type3 test"() {
        when:
        def theme3 = themeReader.readType3Data(File(resources, "type3test", "overlays"))

        then:
        ["Light", "Black", "Dark"] == theme3.extensions.collect { it.name }

    }

    @Ignore
    def File(File init, String... sub) {
        sub.inject(init) { file, s -> new File(file, s) }
    }
}