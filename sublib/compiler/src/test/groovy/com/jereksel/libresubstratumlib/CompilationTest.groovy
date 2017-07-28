package com.jereksel.libresubstratumlib

import spock.lang.Specification

class CompilationTest extends Specification {

    def resources = new File(getClass().classLoader.getResource("resource.json").path).parentFile

    def "Color from apk reading test"() {

        when:
        def apk = new File(resources, "Theme.apk")

        then:
        AAPT.INSTANCE.getColorsValues(apk).iterator().toList() == [new Color("my_color", "0xffabcdef")]
    }

    def "When color is available it's value is returned"() {
        setup:
        def apk = new File(resources, "Theme.apk")

        when:
        def color = AAPT.INSTANCE.getColorValue(apk, "my_color")

        then:
        "0xffabcdef" == color
    }

    def "When color doesn't exist exception is throws"() {
        setup:
        def apk = new File(resources, "Theme.apk")

        when:
        AAPT.INSTANCE.getColorValue(apk, "color_that_doesnt_exist")

        then:
        thrown NoSuchElementException

    }
}