package cn.wthee.pcrtool.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 竞技场收藏
 */
@Entity(tableName = "pvp_like")
data class PvpFavoriteData(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String = "",
    @ColumnInfo(name = "atks") val atks: String = "",
    @ColumnInfo(name = "defs") val defs: String = "",
    @ColumnInfo(name = "atkTalentIds") val atkTalentIds: String = "",
    @ColumnInfo(name = "defTalentIds") val defTalentIds: String = "",
    @ColumnInfo(name = "date") val date: String = "",
    @ColumnInfo(name = "region") val region: Int = 0,
)