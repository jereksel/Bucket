package com.jereksel.libresubstratum.utils

import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension
import io.kotlintest.properties.Row8
import io.kotlintest.specs.FunSpec

class ThemeNameUtilsTest: FunSpec() {

    init {

        test("ThemeNameUtilsTest") {

            val table = table(
                    headers("appId", "themeName", "type1a", "type1b", "type1c", "type2", "type3", "expected"),
//                    row("com.android.settings", "My Theme", null, null, null, null, null, "com.android.settings.MyTheme"),
                    row("com.android.settings", "My Theme", Type1Extension("Type 1a", true), null, null, null, null, "com.android.settings.MyTheme"),
                    row("com.android.settings", "My Theme", null, Type1Extension("Type 1b", true), null, null, null, "com.android.settings.MyTheme"),
                    row("com.android.settings", "My Theme", null, null, Type1Extension("Type 1c", true), null, null, "com.android.settings.MyTheme"),

                    row("com.android.settings", "My Theme", Type1Extension("Type 1a", false), null, null, null, null, "com.android.settings.MyTheme.Type1a"),
                    row("com.android.settings", "My Theme", Type1Extension("Type 1a", false), Type1Extension("Type 1b", false), null, null, null, "com.android.settings.MyTheme.Type1aType1b"),
                    row("com.android.settings", "My Theme", Type1Extension("Type 1a", false), Type1Extension("Type 1b", false), Type1Extension("Type 1c", false), null, null, "com.android.settings.MyTheme.Type1aType1bType1c"),

                    row("com.android.settings", "My Theme", null, null, null, Type2Extension("Type2", true), null, "com.android.settings.MyTheme"),
                    row("com.android.settings", "My Theme", null, null, null, Type2Extension("Type2",false), null, "com.android.settings.MyTheme.Type2"),

                    row("com.android.settings", "My Theme", null, null, null, null, Type3Extension("Type3", true), "com.android.settings.MyTheme"),
                    row("com.android.settings", "My Theme", null, null, null, null, Type3Extension("Type3",false), "com.android.settings.MyTheme.Type3"),

                    row("com.android.settings", "My Theme", null, null, null, Type2Extension("Type2",false), Type3Extension("Type3", false), "com.android.settings.MyTheme.Type2.Type3")

            )

            forAll(table) { appId, themeName, type1a, type1b, type1c, type2, type3, expected ->
                val result = ThemeNameUtils.getTargetOverlayName(appId, themeName, type1a, type1b, type1c, type2, type3)
                result shouldBe expected
            }

        }

    }

    fun row(a: String, b: String, c: Type1Extension?, d: Type1Extension?, e: Type1Extension?, f: Type2Extension?, g: Type3Extension?, h: String) = Row8(a, b, c, d, e, f, g, h)

}