package com.jereksel.libresubstratum.domain.db.themeinfo.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "themepack")
public class RoomThemePack {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    public String themeId;
//    public List<RoomTheme> themes = new ArrayList<>();
//    public List<RoomType3Extension> type3 = new ArrayList<>();
}