package cn.wthee.pcrtool.data.enums;

/**
 * 首页
 */
enum class OverviewType(val id: Int) {
    CHARACTER(0),
    EQUIP(1),
    TOOL(2),
    NEWS(3),
    IN_PROGRESS_EVENT(4),
    COMING_SOON_EVENT(5),
    UNIQUE_EQUIP(6)
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.id == value } ?: CHARACTER
    }
}
