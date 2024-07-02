package cn.wthee.pcrtool.data.enums;

import androidx.compose.ui.graphics.Color
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorRed

/**
 * 角色获取类型
 */
enum class CharacterLimitType(val type: Int, val color: Color, val typeNameId: Int) {
    NORMAL(1, colorGold, R.string.type_normal),
    LIMIT(2, colorRed, R.string.type_limit),
    EVENT(3, colorGreen, R.string.type_event_limit),
    EXTRA(4, colorCyan, R.string.type_extra_character);

    companion object {
        fun getByType(type: Int) = CharacterLimitType.entries
            .find { it.type == type } ?: NORMAL
    }
}