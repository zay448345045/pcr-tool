package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 48：持续治疗
fun SkillActionDetail.hot(): String {
    val type = when (actionDetail2) {
        1 -> getString(R.string.attr_hp)
        2 -> getString(R.string.attr_tp)
        else -> UNKNOWN
    }
    val value = getValueText(1, actionValue1, actionValue2, actionValue3)
    val time = getTimeText(5, actionValue5, actionValue6)
    return if (type != UNKNOWN) {
        getString(R.string.skill_action_type_desc_48, getTarget(), type, value, time)
    } else {
        UNKNOWN
    }
}