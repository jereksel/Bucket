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
        childColumns = "theme_pack_id",
        onDelete = CASCADE),
        indices = {@Index("id"), @Index("theme_pack_id")}
)
public class RoomType3Extension {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    @ColumnInfo(name = "theme_pack_id")
    public Long themePackId;
    public String name;
    public Boolean def;
}