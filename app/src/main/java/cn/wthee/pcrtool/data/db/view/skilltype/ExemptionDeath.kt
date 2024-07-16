package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 71：免死
fun SkillActionDetail.exemptionDeath(): String {
    val value = getValueText(2, actionValue2, actionValue3, actionValue4)
    val time = getTimeText(6, actionValue6, actionValue7)
    return getString(R.string.skill_action_type_desc_71, getTarget(), value, time)
}