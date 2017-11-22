package com.jereksel.libresubstratum.infrastructure.subsdatabase

import com.jereksel.libresubstratum.domain.Pricing.*
import com.jereksel.libresubstratum.domain.SubstratumDatabaseTheme
import com.jereksel.libresubstratum.domain.Support.*
import com.jereksel.libresubstratum.extensions.getLogger
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

object XmlConverter {

    val log = getLogger()

    fun convert(xml: String): List<SubstratumDatabaseTheme> {

        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()

        val `is` = InputSource()
        `is`.characterStream = StringReader(xml)
        val doc = db.parse(`is`)

        return doc.getElementsByTagName("theme")
                .toList()
                .mapNotNull {
                    it as? Element
                }
                .map {
                    val author = it["author"]
                    val packageId = it["package"]
                    val pricing = it["pricing"]
                    val enumPricing = stringToPricing(pricing)
                    val supports = it["support"]
                    val link = it["link"]
                    val enumSupports = supports
                            .split("|")
                            .mapNotNull { stringToSupport(it) }
                    val image = it["image"]
                    val backgroundImage = it["backgroundimage"]

                    SubstratumDatabaseTheme(author, link, packageId, enumPricing,
                            enumSupports, image, backgroundImage)
                }

    }

    private fun stringToPricing(s: String) = when(s) {
        "Free" -> FREE
        "Paid" -> PAID
        else -> {
            throw RuntimeException("Unknown pricing $s")
        }
    }

    private fun stringToSupport(s: String) = when(s) {
        "overlays" -> OVERLAYS
        "fonts" -> FONTS
        "bootanimations" -> BOOTANIMATIONS
        "sounds" -> SOUNDS
        else -> {
            log.warn("Unknown support: $s")
            null
        }
    }

    private fun NodeList.toList() = (0 until length).map { item(it) }

    private operator fun Element.get(s: String) = getElementsByTagName(s).item(0).textContent

}

