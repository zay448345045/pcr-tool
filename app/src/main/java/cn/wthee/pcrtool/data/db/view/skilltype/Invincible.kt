package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.initOtherLimit

// 21：回避
fun SkillActionDetail.invincible(): String {
    //回避等技能限制
    initOtherLimit()
    tag = cn.wthee.pcrtool.utils.getString(
        when (actionDetail1) {
            1 -> cn.wthee.pcrtool.R.string.skill_action_type_desc_21_1
            2 -> cn.wthee.pcrtool.R.string.skill_action_type_desc_21_2
            3 -> cn.wthee.pcrtool.R.string.skill_action_type_desc_21_3
            else -> cn.wthee.pcrtool.R.string.unknown
        }
    )
    return if (actionValue1 > 0) {
        val time = getTimeText(1, actionValue1, actionValue2)
        "$tag$time"
    } else {
        tag
    }
}