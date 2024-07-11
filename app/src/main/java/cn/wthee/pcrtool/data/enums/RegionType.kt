package cn.wthee.pcrtool.data.enums

import cn.wthee.pcrtool.R

/**
 * 区服
 */
enum class RegionType(val value: Int, val stringId: Int, val code: String) {
    CN(2, R.string.db_cn, "cn"),
    TW(3, R.string.db_tw, "tw"),
    JP(4, R.string.db_jp, "jp"),
    ;

    companion object {
        fun getByValue(value: Int) = entries
            .find { it.value == value } ?: CN
    }
}