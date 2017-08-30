package com.jereksel.libresubstratumlib

import spock.lang.Ignore
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
        def file = compileTheme(compilationDir: new File(resources, "basicTheme"))

        then:
        file.exists()
        file.size() > 10
    }

    def "Compiled theme should have all colors"() {
        when:
        def file = compileTheme(compilationDir: new File(resources, "basicTheme"))
        def colors = aapt.getColorsValues(file)

        then:
        colors.iterator().toList() == [new Color("color1", "0xffabcdef"), new Color("color2", "0x12345678")]
    }

    def "Type1 when all default compilation is in place"() {
        given:
        def themeLoc = new File(resources, "type1Theme")
        def compilationDir = Paths.get(resources.absolutePath, "type1Theme", "overlays", "android").toFile()

        when:
        def theme = new ThemeReader().readThemePack(themeLoc)
        def types1 = theme.themes[0].type1.collect { new Type1DataToCompile(it.extension.find { it.default }, it.suffix) }
        def apk = compileTheme(compilationDir: compilationDir, type1: types1)

        then:
        apk.exists()
        !new File(temp, "overlay").exists()
    }

    def "Type1 type1.xml should be replaced"() {
        given:
        def compilationDir = Paths.get(resources.absolutePath, "type1Theme", "overlays", "android").toFile()

        when:
        def types1 = [new Type1DataToCompile(new Type1Extension("ATYPE1", false), "a")]
        def apk = compileTheme(compilationDir: compilationDir, type1: types1)
        def colors = aapt.getColorsValues(apk)

        then:
        apk.exists()
        new File(temp, "overlay").exists()
        colors.iterator().toList() == [new Color("color1", "0x12345678"), new Color("color2", "0x00000000"), new Color("color3", "0x00000000")]
    }

    def "Type1 multiple types test"() {
        given:
        def compilationDir = Paths.get(resources.absolutePath, "type1Theme", "overlays", "android").toFile()

        when:
        def types1 = [new Type1DataToCompile(new Type1Extension("BTYPE1", false), "b"), new Type1DataToCompile(new Type1Extension("CTYPE2", false), "c"),]
        def apk = compileTheme(compilationDir: compilationDir, type1: types1)
        def colors = aapt.getColorsValues(apk)

        then:
        apk.exists()
        new File(temp, "overlay").exists()
        colors.iterator().toList() == [new Color("color1", "0x00000000"), new Color("color2", "0x12345678"), new Color("color3", "0xffffffff")]

    }

    def "Type2 with default value compilation test"() {
        given:
        def themeLoc = new File(resources, "type2Theme")
        def compilationDir = Paths.get(resources.absolutePath, "type2Theme", "overlays", "android").toFile()

        when:
        def theme = new ThemeReader().readThemePack(themeLoc)
        def type2 = theme.themes[0].type2.extensions[0]
        def apk = compileTheme(compilationDir: compilationDir, type2: type2)
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
        def apk = compileTheme(compilationDir: compilationDir, type2: type2)
        def colors = aapt.getColorsValues(apk)

        then:
        type2 != null
        apk.exists()
        colors.iterator().toList() == [new Color("color1", "0xffffffff"), new Color("color2", "0x00abcdef")]
    }


    def "Type3 with default value compilation test"() {
        given:
        def themeLoc = new File(resources, "type3Theme")
        def compilationDir = Paths.get(resources.absolutePath, "type3Theme", "overlays", "android").toFile()

        when:
        def theme = new ThemeReader().readThemePack(themeLoc)
        def type3 = theme.type3.extensions[0]
        def apk = compileTheme(compilationDir: compilationDir, type3: type3)
        def colors = aapt.getColorsValues(apk)

        then:
        type3 != null
        apk.exists()
        colors.iterator().toList() == [new Color("color1", "0x00000000"), new Color("color2", "0x00abcdef")]
    }

    def "Type3 with non default value compilation test"() {
        given:
        def themeLoc = new File(resources, "type3Theme")
        def compilationDir = Paths.get(resources.absolutePath, "type3Theme", "overlays", "android").toFile()

        when:
        def theme = new ThemeReader().readThemePack(themeLoc)
        def type3 = theme.type3.extensions[1]
        def apk = compileTheme(compilationDir: compilationDir, type3: type3)
        def colors = aapt.getColorsValues(apk)

        then:
        type3 != null
        apk.exists()
        colors.iterator().toList() == [new Color("color1", "0xffffffff"), new Color("color2", "0x00abcdef")]
    }

    def "IllegalArgumentException is thrown when folder with theme doesn't exist"() {
        given:
        def folder = new File(resources, "asdasdasd")
        folder.deleteDir()

        when:
        compileTheme(compilationDir: folder)

        then:
        thrown IllegalArgumentException

    }

    def "IllegalArgumentException is thrown when temp folder doesn't exist"() {
        given:
        def folder = new File(resources, "asdadaddassdasda")
        folder.deleteDir()

        when:
        compileTheme(compilationDir: folder)

        then:
        thrown IllegalArgumentException

    }

    @Ignore
    def compileTheme(args) {
        def type1 = args["type1"] ?: []
        def type2 = args["type2"] ?: null
        def type3 = args["type3"] ?: null
        def compilationPath = args["compilationDir"]
        return aapt.compileTheme(new ThemeToCompile("com.app.app", "com.app.app", "com.app.app", type1, type2, type3), compilationPath, temp, [])
    }

}