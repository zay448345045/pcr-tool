package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

// 56：千里眼
fun SkillActionDetail.countBlind() = when (actionValue1.toInt()) {
    1 -> {
        val time = getTimeText(2, actionValue2, actionValue3)
        getString(R.string.skill_action_type_desc_56_1, time)
    }

    2 -> {
        val value = getValueText(2, actionValue2, actionValue3)
        getString(R.string.skill_action_type_desc_56_2, getTarget(), value)
    }

    else -> UNKNOWN
}
