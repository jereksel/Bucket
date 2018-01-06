/*
 * Copyright (C) 2018 Andrzej Ressel (jereksel@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jereksel.libresubstratum.domain.overlayService.oreo

import com.google.common.collect.ArrayListMultimap
import com.jereksel.libresubstratum.domain.OverlayInfo
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat

@Suppress("IllegalIdentifier")
class OreoOverlayReaderTest {

    @Test
    fun `Read test`() {

        val output = """
            android
            [x] com.google.android.theme.pixel
            [ ] com.google.android.theme.stock

            com.android.systemui
            [ ] com.android.systemui.theme.dark
        """.trimIndent()

        val expected = ArrayListMultimap.create<String, OverlayInfo>()
        with(expected) {
            put("android", OverlayInfo("com.google.android.theme.pixel", "android", true))
            put("android", OverlayInfo("com.google.android.theme.stock", "android", false))
            put("com.android.systemui", OverlayInfo("com.android.systemui.theme.dark", "com.android.systemui", false))
        }

        val result = OreoOverlayReader.read(output)

        assertThat(result).isEqualTo(expected)

    }

}