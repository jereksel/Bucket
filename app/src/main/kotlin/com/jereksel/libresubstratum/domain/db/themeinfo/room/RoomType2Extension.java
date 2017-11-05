package com.jereksel.libresubstratum.domain.db.themeinfo.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys =
@ForeignKey(entity = RoomTheme.class,
        parentColumns = "id",
        childColumns = "theme_id",
        onDelete = CASCADE),
        indices = {@Index("id"), @Index("theme_id")}
)
public class RoomType2Extension {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    @ColumnInfo(name = "theme_id")
    public Long themeId;
    public String name;
    public Boolean def;
}