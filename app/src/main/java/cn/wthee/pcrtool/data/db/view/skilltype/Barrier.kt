package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getBarrierType
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 6：护盾
fun SkillActionDetail.barrier(): String {
    val value = getValueText(1, actionValue1, actionValue2)
    val time = getTimeText(3, actionValue3, actionValue4)
    val type = getBarrierType(actionDetail1)
    return if (type != UNKNOWN) {
        getString(R.string.skill_action_type_desc_6, getTarget(), type, value, time)
    } else {
        type
    }
}

// 73：伤害护盾
fun SkillActionDetail.logBarrier(): String {
    val time = getTimeText(3, actionValue3, actionValue4)
    return getString(
        R.string.skill_action_type_desc_73,
        getTarget(),
        actionValue5.toInt(),
        time
    )
}