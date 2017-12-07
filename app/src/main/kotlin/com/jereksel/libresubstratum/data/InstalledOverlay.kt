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

package com.jereksel.libresubstratum.data

import android.graphics.drawable.Drawable

data class InstalledOverlay (
        val overlayId: String,
        //Source theme
        val sourceThemeId: String,
        val sourceThemeName: String,
        val sourceThemeDrawable: Drawable?,
        //Target app
        val targetId: String,
        val targetName: String,
        val targetDrawable: Drawable?,
        val type1a: String? = null,
        val type1b: String? = null,
        val type1c: String? = null,
        val type2: String? = null,
        val type3: String? = null
)
