package com.jereksel.libresubstratumlib

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

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

    def "Type2 with default value compilation test"() {
        given:
        def themeLoc = new File(resources, "type2Theme")
        def compilationDir = Paths.get(resources.absolutePath, "type2Theme", "overlays", "android").toFile()

        when:
        def theme = new ThemeReader().readThemePack(themeLoc)
        def type2 = theme.themes[0].type2.extensions[0]
        def apk = aapt.compileTheme(new ThemeToCompile("a", [], type2, null), compilationDir, temp)
        def colors = aapt.getColorsValues(apk)

        then:
        type2 != null
        apk.exists()
        colors.iterator().toList() == [new Color("color1", "0x00000000"), new Color("color2", "0x00abcdef")]

    }

    def "Type2 with non default value compilation test"() {
        given:
        def themeLoc = new File(resources, "type2Theme")
        def compilationDir = Paths.get(resources.absolutePath, "type2Theme", "overlays", "android").toFile()

        when:
        def theme = new ThemeReader().readThemePack(themeLoc)
        def type2 = theme.themes[0].type2.extensions[1]
        def apk = aapt.compileTheme(new ThemeToCompile("a", [], type2, null), compilationDir, temp)
        def colors = aapt.getColorsValues(apk)

        then:
        type2 != null
        apk.exists()
        colors.iterator().toList() == [new Color("color1", "0xffffffff"), new Color("color2", "0x00abcdef")]

    }
}