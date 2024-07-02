package cn.wthee.pcrtool.data.enums;

/**
 * 角色评级
 */
enum class LeaderTierType(val type: Int) {
    ALL(0),
    PVP_ATK(1),
    PVP_DEF(2),
    CLAN(3);

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.type == value } ?: ALL
    }
}