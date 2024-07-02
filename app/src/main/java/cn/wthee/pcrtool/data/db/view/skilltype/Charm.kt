package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.data.enums.SkillActionType
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

//11：魅惑/混乱12：黑暗 13：沉默
fun SkillActionDetail.charm(): String {
    val chance =
        getValueText(
            3,
            actionValue3,
            if (actionValue3.toInt() == 100) 0.0 else 1.0,
            0.0,
            percent = "%"
        )
    val time = getTimeText(1, actionValue1, actionValue2)
    if (SkillActionType.getByType(actionType) == SkillActionType.CHARM) {
        tag = getString(
            when (actionDetail1) {
                0 -> R.string.skill_charm_0
                1 -> R.string.skill_charm_1
                else -> R.string.unknown
            }
        )
    }

    return getString(
        R.string.skill_action_type_desc_12_13,
        chance,
        getTarget(),
        tag,
        time
    ) + if (actionType == 12) {
        getString(R.string.skill_action_atk_miss, 100 - actionDetail1)
    } else {
        ""
    }
}