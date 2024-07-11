package cn.wthee.pcrtool.data.enums

import androidx.compose.ui.graphics.Color
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.colorRed

/**
 * 站位
 */
enum class PositionType(val type: Int, val typeNameId: Int, val color: Color, val iconId: Int) {
    UNKNOWN(0, R.string.unknown, colorGray, R.drawable.unknown_item),
    POSITION_FRONT(1, R.string.position_0, colorRed, R.drawable.ic_position_0),
    POSITION_MIDDLE(2, R.string.position_1, colorGold, R.drawable.ic_position_1),
    POSITION_BACK(2, R.string.position_2, colorCyan, R.drawable.ic_position_2), ;

    companion object {
        fun getPositionType(position: Int) = when (position) {
            in 1..299 -> POSITION_FRONT
            in 300..599 -> POSITION_MIDDLE
            in 600..9999 -> POSITION_BACK
            else -> UNKNOWN
        }
    }
}