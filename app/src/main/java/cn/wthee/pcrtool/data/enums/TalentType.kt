package cn.wthee.pcrtool.data.enums

import androidx.compose.ui.graphics.Color
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed

/**
 * 天赋类型
 */
enum class TalentType(val type: Int, val color: Color, val typeNameId: Int) {
    ALL(0, Color.Unspecified, R.string.talent),
    FIRE(1, colorRed, R.string.fire),
    WATER(2, colorCyan, R.string.water),
    WIND(3, colorGreen, R.string.wind),
    LIGHT(4, colorGold, R.string.light),
    DARK(5, colorPurple, R.string.dark),
    ;

    companion object {
        fun getByType(type: Int) = TalentType.entries
            .find { it.type == type } ?: ALL
    }
}