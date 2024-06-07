package cn.wthee.pcrtool.data.enums

/**
 * 角色单位类型
 */
enum class UnitType(val type: Int) {
    CHARACTER(0),
    CHARACTER_SUMMON(1),
    ENEMY(2),
    ENEMY_SUMMON(3);

    companion object {
        fun getByValue(value: Int) = entries.find { it.type == value } ?: CHARACTER
    }
}