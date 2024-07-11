package cn.wthee.pcrtool.data.enums

import androidx.compose.ui.graphics.Color
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.colorPurple

/**
 * 攻击类型
 */
enum class AtkType(val type: Int, val color: Color, val typeNameId: Int, val iconId: Int) {
    UNKNOWN(0, colorGray, R.string.unknown, R.drawable.unknown_item),
    PHYSICAL(1, colorGold, R.string.physical, R.drawable.ic_atk_type_1),
    MAGIC(2, colorPurple, R.string.magic, R.drawable.ic_atk_type_2)
    ;

    companion object {
        fun getByType(type: Int) = AtkType.entries
            .find { it.type == type } ?: UNKNOWN
    }
}