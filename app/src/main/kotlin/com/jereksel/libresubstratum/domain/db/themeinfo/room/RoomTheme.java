package com.jereksel.libresubstratum.domain.db.themeinfo.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class RoomTheme {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    public String targetId;
    public List<RoomType1aExtension> type1a = new ArrayList<>();
    public List<RoomType1bExtension> type1b = new ArrayList<>();
    public List<RoomType1cExtension> type1c = new ArrayList<>();
    public List<RoomType2Extension> type2 = new ArrayList<>();
}
