package com.jereksel.libresubstratum.domain.db.themeinfo.room;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class RoomType2Extension {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    public String name;
    public Boolean def;
}