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