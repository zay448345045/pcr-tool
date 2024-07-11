package cn.wthee.pcrtool.data.enums

/**
 * 活动日程类型
 */
enum class CalendarEventType(val type: Int) {
    UNKNOWN(404),
    TOWER(1),
    SP_DUNGEON(-1),
    TDF(-2),
    COLOSSEUM(-3),
    DAILY(18),
    LOGIN(19),
    FORTUNE(20),
    N_DROP(31),
    N_MANA(41),
    H_DROP(32),
    H_MANA(42),
    VH_DROP(39),
    VH_MANA(49),
    EXPLORE(34),
    SHRINE(37),
    TEMPLE(38),
    DUNGEON(45),
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.type == value } ?: UNKNOWN
    }
}
