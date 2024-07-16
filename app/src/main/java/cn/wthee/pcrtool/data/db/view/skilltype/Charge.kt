package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getValueText

// 18：蓄力、19：伤害充能
fun SkillActionDetail.charge(): String {
    val desc = getString(
        R.string.skill_action_type_desc_18_19,
        actionValue3.toString()
    )

    val extraDesc = if (actionValue1 != 0.0 || actionValue2 != 0.0) {
        val value = getValueText(1, actionValue1, actionValue2)
        getString(
            R.string.skill_action_type_desc_18_19_detail,
            actionDetail2 % 100,
            value
        )
    } else {
        ""
    }
    return desc + extraDesc
}