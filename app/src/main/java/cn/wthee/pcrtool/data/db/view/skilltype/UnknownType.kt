package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getPercent
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText

fun SkillActionDetail.unknownType(): String {
    val value = getValueText(
        1,
        actionValue1,
        actionValue2,
        percent = getPercent()
    )
    val time = getTimeText(3, actionValue3, actionValue4)

    return getString(
        R.string.skill_action_type_unknown,
        UNKNOWN,
        getTarget(),
        actionType,
        value,
        time
    )
}