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

package com.jereksel.libresubstratum.views;

import android.support.annotation.VisibleForTesting;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.jereksel.libresubstratum.data.Type1ExtensionToString;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface ITypeView {
    void setSelection(int var1);

    void setType1(@NotNull List<Type1ExtensionToString> list);

    void onPositionChange(@NotNull ITypeView.TypeViewSelectionListener listener);

    @VisibleForTesting
    @NotNull
    Spinner getSpinner();

    @VisibleForTesting
    @NotNull
    SeekBar getSeekBar();

    public interface TypeViewSelectionListener {
        void onPositionChange(int var1);
    }
}