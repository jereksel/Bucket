/*
 * Copyright (C) 2017 Andrzej Ressel (jereksel@gmail.com)
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

package com.jereksel.libresubstratum.data.libraries

import com.jereksel.libresubstratum.R

class Library(
        val name: String,
        val license: LicenseType,
        vararg val authors: Author
)

enum class LicenseType(val licenseType: String, val stringId: Int) {
    MIT("MIT", R.string.license_mit),
    APACHE2("Apache2", R.string.license_apache2),
    EPL("Eclipse Public License", R.string.license_epl)
}

data class Author(
        val name: String,
        val year: String
//        val startYear: Long,
//        val endYear: Long
)