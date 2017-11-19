package com.jereksel.libresubstratum.infrastruture.subsdatabase

import com.jereksel.libresubstratum.domain.Support.*
import com.jereksel.libresubstratum.infrastructure.subsdatabase.XmlConverter
import io.kotlintest.specs.FunSpec
import org.assertj.core.api.Assertions.assertThat

class XmlConverterTest: FunSpec() {


    init {

        test("Support mapping") {

            val xml = """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
                <substratum>
                    <theme id="Beltz Theme">
                        <author>Jimmy Setiawan</author>
                        <link>https://play.google.com/store/apps/details?id=bitsykolayers.Midnight</link>
                        <package>bitsykolayers.Midnight</package>
                        <pricing>Paid</pricing>
                        <support>overlays|fonts|bootanimations|wallpapers</support>
                        <backgroundimage>https://raw.githubusercontent.com/substratum/database/master/images/beltz.png</backgroundimage>
                        <image>http://lh3.googleusercontent.com/jMWFY93hzT-Wrwo06Y0uCOXwGCy8D0cd2uDBR4MkGswOj0BGatEDBRmJIOnN3TjfTjg=w300-rw</image>
                    </theme>
                </substratum>
                """

            val theme = XmlConverter.convert(xml)

            assertThat(theme).hasSize(1)
            assertThat(theme[0].support).containsExactly(OVERLAYS, FONTS, BOOTANIMATIONS)

        }

        test("Empty support field") {

            val xml = """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
                <substratum>
                    <theme id="Theme Ready Google Apps">
                        <author>Team Black Out</author>
                        <link>http://forum.xda-developers.com/android/apps-games/apps-themeable-hangouts-layers-cmte-t3113192</link>
                        <package/>
                        <pricing>Free</pricing>
                        <backgroundimage>https://raw.githubusercontent.com/substratum/database/master/images/themereadygapps.jpg</backgroundimage>
                        <image>https://github.com/Train88/TeamBlackOut/blob/master/app/src/main/res/mipmap-xxhdpi/ic_launcher.png?raw=true</image>
                        <support/>
                    </theme>
                </substratum>
                """

            val theme = XmlConverter.convert(xml)
            assertThat(theme).hasSize(1)
            assertThat(theme[0].support).isEmpty()

        }

    }


}