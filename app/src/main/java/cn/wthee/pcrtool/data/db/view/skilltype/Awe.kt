package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 62：畏惧
fun SkillActionDetail.awe(): String {
    val value = getValueText(1, actionValue1, actionValue2, 0.0, percent = "%")
    val time = getTimeText(3, actionValue3, actionValue4)
    return when (actionDetail1) {
        0 -> getString(R.string.skill_action_type_desc_62_0, getTarget(), value, time)
        1 -> getString(R.string.skill_action_type_desc_62_1, getTarget(), value, time)
        else -> UNKNOWN
    }
}