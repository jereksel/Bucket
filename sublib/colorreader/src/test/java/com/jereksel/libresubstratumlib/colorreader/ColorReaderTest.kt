package com.jereksel.libresubstratumlib.colorreader

import io.kotlintest.specs.FunSpec
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import java.io.File

class ColorReaderTest: FunSpec() {

    val aapt = File("${System.getenv("ANDROID_HOME")}/build-tools/26.0.2/aapt")

    val resources = File(javaClass.classLoader.getResource("Theme.apk").path).parentFile

    val apk = File(resources, "Theme.apk")

    init {

        test("Color from apk reading test") {

            val colors = ColorReader.getColorsValues(aapt, apk)

            assertThat(colors).containsExactlyInAnyOrder(Color("my_color", "0xffabcdef"))

        }

        test("When color is available it's value is returned") {

            val color = ColorReader.getColorValue(aapt, apk, "my_color")

            assertThat(color).isEqualTo("0xffabcdef")

        }

        test("When color doesn't exist exception is throws") {

            assertThatThrownBy { ColorReader.getColorValue(aapt, apk, "color_that_doesnt_exist") }
                    .isInstanceOf(NoSuchElementException::class.java)


        }



    }

}