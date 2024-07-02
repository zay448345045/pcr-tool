package cn.wthee.pcrtool.data.enums;

import cn.wthee.pcrtool.R


/**
 * 角色排行排序类型枚举
 */
enum class LeaderboardSortType(val type: Int, val titleId: Int) {
    ALL(type = 0, titleId = R.string.all),
    TALENT(type = 1, titleId = R.string.talent),
    PVP(type = 2, titleId = R.string.jjc),
    CLAN_BATTLE(type = 3, titleId = R.string.clan),
    ;

    companion object {
        fun getByType(type: Int) = entries
            .find { it.type == type } ?: ALL
    }
}