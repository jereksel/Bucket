package com.jereksel.libresubstratumlib

import spock.lang.Specification

class ThemeReaderExtractlessTest extends Specification {

    def resources = new File(getClass().classLoader.getResource("resource.json").path).parentFile

    def themeReader = new ThemeReaderExtractless()

    def "VerySimpleTheme test"() {
        when:
        def themePack = themeReader.readThemePack(new File(resources, "VerySimpleTheme.zip"))

        then:
        ["android", "com.android.settings", "com.android.systemui"] == themePack.themes.collect {it.application}.sort()
    }

    def "simple type1 test"() {
        when:
        def themePack = themeReader.readThemePack(new File(resources, "Type1Test.zip"))

        then:
        true
        ["Initial color", "Green", "Red"] == themePack.themes[0].type1.get(0).extension.collect { it.name }
    }

    def "simple type2 test"() {
        when:
        def theme2 = themeReader.readThemePack(new File(resources, "Type2Test.zip"))

        then:
        ["Light", "Black", "Dark"] == theme2.themes[0].type2.extensions.collect { it.name }
    }

    def "simple type2 empty theme test"() {
        when:
        def theme2 = themeReader.readThemePack(new File(resources, "VerySimpleTheme.zip"))

        then:
        null == theme2.themes[0].type2
    }

    def "Type3 test"() {
        when:
        def themePack = themeReader.readThemePack(new File(resources, "Type3Test.zip"))

        then:
        ["Light", "Black", "Dark"] == themePack.type3.extensions.collect { it.name }
    }
}