package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTimeText

// 75：次数触发
fun SkillActionDetail.hitCount(): String {
    val time = getTimeText(3, actionValue3, actionValue4)

    return when (actionDetail1) {
        3 -> getString(
            R.string.skill_action_type_desc_75,
            actionValue1.toInt(),
            actionDetail2 % 100,
            time
        )

        else -> UNKNOWN
    }
}