package com.jereksel.libresubstratumlib

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files

class CompilationTest extends Specification {

    @Shared
    def resources = new File(getClass().classLoader.getResource("resource.json").path).parentFile

    @Shared
    def aapt = TestAaptFactory.get()

    def temp = Files.createTempDirectory(null).toFile()

    def cleanup() {
        temp.deleteDir()
    }

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

    def "Theme compilation should be successful"() {
        when:
        def file = aapt.compileTheme("com.jereksel.testtheme", new File(resources, "basicTheme"), temp)

        then:
        file.exists()
        file.size() > 10
    }

    def "Compiled theme should have all colors"() {
        when:
        def file = aapt.compileTheme("com.jereksel.testtheme", new File(resources, "basicTheme"), temp)
        def colors = aapt.getColorsValues(file)

        then:
        colors.iterator().toList() == [new Color("color1", "0xffabcdef"), new Color("color2", "0x12345678")]
    }
}