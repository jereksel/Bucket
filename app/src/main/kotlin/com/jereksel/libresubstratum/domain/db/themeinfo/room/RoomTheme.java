package com.jereksel.libresubstratum.domain.db.themeinfo.room;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys =
@ForeignKey(entity = RoomThemePack.class,
        parentColumns = "id",
        childColumns = "theme_pack_id",
        onDelete = CASCADE),
        tableName = "theme",
        indices = {@Index("id"), @Index("theme_pack_id")})
public class RoomTheme {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    @ColumnInfo(name = "theme_pack_id")
    public Long themePackId;
    public String targetId;

//    @Embedded
//    RoomType1aExtension type1aExtension;
//    @Embedded
//    RoomType1bExtension type1bExtension;
//    @Embedded
//    RoomType1cExtension type1cExtension;
//    @Embedded
//    RoomType2Extension type2Extension;
//    @Embedded
//    RoomType3Extension type3Extension;
//    public List<RoomType1aExtension> type1a = new ArrayList<>();
//    public List<RoomType1bExtension> type1b = new ArrayList<>();
//    public List<RoomType1cExtension> type1c = new ArrayList<>();
//    public List<RoomType2Extension> type2 = new ArrayList<>();
}
