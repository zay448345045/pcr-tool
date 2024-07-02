package cn.wthee.pcrtool.data.enums;

import androidx.compose.ui.graphics.Color
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.colorBlue
import cn.wthee.pcrtool.ui.theme.colorCopper
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorOrange
import cn.wthee.pcrtool.ui.theme.colorPink
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorSilver

/**
 * 装备品级、RANk 颜色
 */
enum class RankColor(val type: Int, val color: Color, val typeNameId: Int) {
    UNKNOWN(0, colorGray, R.string.unknown),
    BLUE(1, colorBlue, R.string.color_blue),
    COPPER(2, colorCopper, R.string.color_copper),
    SILVER(3, colorSilver, R.string.color_silver),
    GOLD(4, colorGold, R.string.color_gold),
    PURPLE(5, colorPurple, R.string.color_purple),
    RED(6, colorRed, R.string.color_red),
    GREEN(7, colorGreen, R.string.color_green),
    ORANGE(8, colorOrange, R.string.color_orange),
    CYAN(9, colorCyan, R.string.color_cyan),
    PINK(10, colorPink, R.string.color_pink),
    ;

    companion object {
        fun getByType(type: Int) = RankColor.entries
            .find { it.type == type } ?: UNKNOWN


        /**
         * rank 颜色
         * @param rank rank数值
         */
        fun getRankColor(rank: Int): Color {
            return when (rank) {
                1 -> BLUE.color
                in 2..3 -> COPPER.color
                in 4..6 -> SILVER.color
                in 7..10 -> GOLD.color
                in 11..17 -> PURPLE.color
                in 18..20 -> RED.color
                in 21..23 -> GREEN.color
                in 24..27 -> ORANGE.color
                in 28..99 -> CYAN.color
                else -> UNKNOWN.color
            }
        }
    }
}