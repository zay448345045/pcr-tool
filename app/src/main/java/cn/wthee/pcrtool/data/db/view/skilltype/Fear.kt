package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 61：恐慌
fun SkillActionDetail.fear(ailmentName: String): String {
    val value = getValueText(3, actionValue3, actionValue4, 0.0, percent = "%")
    val time = getTimeText(1, actionValue1, actionValue2)
    return getString(R.string.skill_action_type_desc_61, value, getTarget(), ailmentName, time)
}