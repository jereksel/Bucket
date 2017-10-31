package com.jereksel.libresubstratum.utils

import com.jereksel.libresubstratumlib.Type1Extension
import com.jereksel.libresubstratumlib.Type2Extension
import com.jereksel.libresubstratumlib.Type3Extension

//TODO: Move it to domain (maybe?)
object ThemeNameUtils {

    fun getTargetOverlayName(
            appId: String,
            themeName: String,
//            themeId: String,
            type1a: Type1Extension?,
            type1b: Type1Extension?,
            type1c: Type1Extension?,
            type2: Type2Extension?,
            type3: Type3Extension?
    ): String {

//        val themeName = packageManager.getAppName(themeId)

        val suffix = listOf(type1a, type1b, type1c)
                .filter { it?.default == false }
                .mapNotNull { it?.name }
                .joinToString(separator = "")

        val type1String = if (suffix.isNotEmpty()) { ".$suffix" } else { "" }
        val type2String = if (type2?.default == false) { ".${type2.name}"  } else { "" }
        val type3String = if (type3?.default == false) { ".${type3.name}" } else { "" }

        return "$appId.$themeName$type1String$type2String$type3String".replace(" ", "")
                .replace("-", "").replace("_", "").replace("/", "").replace("#", "")
                .replace("[", "").replace("]", "").replace("(", "").replace(")", "")
                .replace("!", "").replace("+", "")
    }

}