package cn.wthee.pcrtool.data.enums

import cn.wthee.pcrtool.R

/**
 * 视频类型
 */
enum class VideoType(val value: Int, val typeName: Int) {
    UNKNOWN(0, R.string.unknown),
    UB_SKILL(1, R.string.union_burst),
    CHARACTER_CARD(2, R.string.character),
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.value == value } ?: UNKNOWN
    }
}