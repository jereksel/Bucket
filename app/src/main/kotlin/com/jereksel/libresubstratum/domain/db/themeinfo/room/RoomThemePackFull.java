package com.jereksel.libresubstratum.domain.db.themeinfo.room;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class RoomThemePackFull {
    @Embedded
    public RoomThemePack themePack;
    @Relation(parentColumn = "id", entityColumn = "theme_pack_id")
    List<RoomTheme> themeList;
}
