package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.R


//rank 颜色
fun getRankColor(rank: Int): Int {
    val color = when (rank) {
        in 2..3 -> R.color.color_rank_2_3
        in 4..6 -> R.color.color_rank_4_6
        in 7..10 -> R.color.color_rank_7_10
        in 11..17 -> R.color.color_rank_11_17
        in 18..99 -> R.color.color_rank_18
        else -> {
            R.color.color_rank_2_3
        }
    }
    return ResourcesUtil.getColor(color)
}

/**
 * Rank 格式化
 */
fun getFormatText(rank: Int, preStr: String = Constants.RANK_UPPER): String {
    val text = when (rank) {
        in 0..9 -> "  $rank"
        else -> "$rank"

    }
    return "$preStr $text"
}

/**
 * 阶段格式化
 */
fun getSectionText(section: Int): String {
    val text = when (section) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "七"
        else -> section

    }
    return "${text}阶段"
}

/**
 * 获取团队战阶段字体颜色
 */
fun getSectionTextColor(section: Int): Int {
    val color = when (section) {
        1 -> R.color.color_rank_2_3
        2 -> R.color.color_rank_4_6
        3 -> R.color.color_rank_7_10
        4 -> R.color.color_rank_11_17
        else -> R.color.color_rank_18
    }
    return ResourcesUtil.getColor(color)
}