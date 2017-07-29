package com.jereksel.libresubstratumlib

import spock.lang.Specification

class CompilationTest extends Specification {

    def resources = new File(getClass().classLoader.getResource("resource.json").path).parentFile
    def aapt = TestAaptFactory.get()

    def "Color from apk reading test"() {

        when:
        def apk = new File(resources, "Theme.apk")

        then:
        aapt.getColorsValues(apk).iterator().toList() == [new Color("my_color", "0xffabcdef")]
    }

    def "When color is available it's value is returned"() {
        setup:
        def apk = new File(resources, "Theme.apk")

        when:
        def color = aapt.getColorValue(apk, "my_color")

        then:
        "0xffabcdef" == color
    }

    def "When color doesn't exist exception is throws"() {
        setup:
        def apk = new File(resources, "Theme.apk")

        when:
        aapt.getColorValue(apk, "color_that_doesnt_exist")

        then:
        thrown NoSuchElementException

    }
}