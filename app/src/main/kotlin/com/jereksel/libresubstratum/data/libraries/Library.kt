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