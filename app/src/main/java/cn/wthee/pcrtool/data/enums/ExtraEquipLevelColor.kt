package cn.wthee.pcrtool.data.enums;

import androidx.compose.ui.graphics.Color
import cn.wthee.pcrtool.ui.theme.colorCopper
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.colorPink
import cn.wthee.pcrtool.ui.theme.colorSilver
import cn.wthee.pcrtool.utils.Constants

/**
 * ex装备品级
 */
enum class ExtraEquipLevelColor(val type: Int, val color: Color, val typeName: String) {
    UNKNOWN(0, colorGray, Constants.UNKNOWN),
    COPPER(1, colorCopper, "★1"),
    SILVER(2, colorSilver, "★2"),
    GOLD(3, colorGold, "★3"),
    PINK(4, colorPink, "★4"),
    ;

    companion object {
        fun getByType(type: Int) = ExtraEquipLevelColor.entries
            .find { it.type == type } ?: UNKNOWN
    }
}