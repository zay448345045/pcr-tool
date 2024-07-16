package cn.wthee.pcrtool.data.enums

/**
 * 角色排序
 */
enum class CharacterSortType(val type: Int) {
    SORT_DATE(0),
    SORT_AGE(1),
    SORT_HEIGHT(2),
    SORT_WEIGHT(3),
    SORT_POSITION(4),
    SORT_BIRTHDAY(5),
    SORT_UNLOCK_6(6);

    companion object {
        fun getByType(type: Int) = CharacterSortType.entries
            .find { it.type == type } ?: SORT_DATE
    }
}

