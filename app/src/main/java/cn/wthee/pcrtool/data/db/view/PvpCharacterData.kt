package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore

/**
 *  角色位置
 */
data class PvpCharacterData(
    @ColumnInfo(name = "unit_id") var unitId: Int = -1,
    @ColumnInfo(name = "position") var position: Int = 999,
    @ColumnInfo(name = "type") var type: Int = -1,
    @ColumnInfo(name = "talent_id") var talentId: Int = 0,
    @Ignore var count: Int = 0
)
