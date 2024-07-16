package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getPercent
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 4：回复 HP
fun SkillActionDetail.heal(): String {
    val value = getValueText(2, actionValue2, actionValue3, actionValue4)
    return getString(R.string.skill_action_type_desc_4, getTarget(), value)
}

// 59：回复妨碍
fun SkillActionDetail.inhibitHeal(): String {
    val time = getTimeText(2, actionValue2)
    return getString(
        R.string.skill_action_type_desc_59,
        getTarget(),
        (actionValue1 * 100).toInt(),
        time
    )
}

// 76：HP 回复量变化
fun SkillActionDetail.healDown(): String {
    val value =
        getValueText(1, actionValue1, actionValue2, percent = getPercent())
    val time = getTimeText(3, actionValue3, actionValue4)
    return getString(R.string.skill_action_type_desc_76, getTarget(), value, time)
}