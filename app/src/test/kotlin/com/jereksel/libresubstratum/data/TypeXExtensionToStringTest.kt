package com.jereksel.libresubstratum.data

import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension
import io.kotlintest.specs.FunSpec

import org.assertj.core.api.Assertions.*

class TypeXExtensionToStringTest: FunSpec() {

    init {

        test("Underscores should be replaced with spaces") {

            val name = "This_is_name"
            val expected = "This is name"

            extensionFactory(name).toList().forEach {
                assertThat(it.toString()).isEqualTo(expected)
            }

        }

    }



    private fun extensionFactory(name: String): Triple<Type1ExtensionToString, Type2ExtensionToString, Type3ExtensionToString> {

        return Triple(
                Type1ExtensionToString(Type1Extension(name, false)),
                Type2ExtensionToString(Type2Extension(name, false)),
                Type3ExtensionToString(Type3Extension(name, false))
        )

    }

}