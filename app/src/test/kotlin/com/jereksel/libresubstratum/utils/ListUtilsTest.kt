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

package com.jereksel.libresubstratum.utils

import io.kotlintest.specs.FunSpec
import com.jereksel.libresubstratum.utils.ListUtils.replace
import org.assertj.core.api.Assertions.assertThat

class ListUtilsTest: FunSpec() {

    init {

        test("Replace with function") {

            val l = listOf("a", "ab", "abc")

            val l2 = l.replace({it.length == 3}, {it + "d"})

            assertThat(l2).containsExactly("a", "ab", "abcd")

        }

        test("Replace with index") {

            val l = listOf("a", "ab", "abc")

            val l2 = l.replace(0, {it + "d"})

            assertThat(l2).containsExactly("ad", "ab", "abc")

        }


    }


}