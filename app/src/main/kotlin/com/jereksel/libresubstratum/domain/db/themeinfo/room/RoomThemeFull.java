package com.jereksel.libresubstratum.domain.db.themeinfo.room;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class RoomThemeFull {
    @Embedded
    public RoomTheme theme;
    @Relation(parentColumn = "id", entityColumn = "theme_id")
    List<RoomType1aExtension> type1aExtension;
    @Relation(parentColumn = "id", entityColumn = "theme_id")
    List<RoomType1bExtension> type1bExtension;
    @Relation(parentColumn = "id", entityColumn = "theme_id")
    List<RoomType1cExtension> type1cExtension;
    @Relation(parentColumn = "id", entityColumn = "theme_id")
    List<RoomType2Extension> type2Extension;
}
