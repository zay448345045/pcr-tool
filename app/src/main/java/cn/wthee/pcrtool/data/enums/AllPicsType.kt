package cn.wthee.pcrtool.data.enums

/**
 * 立绘类型
 */
enum class AllPicsType(val type: Int) {
    CHARACTER(0),
    STORY(1);

    companion object {
        fun getByValue(value: Int) = entries.find { it.type == value } ?: CHARACTER
    }
}