package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTimeText

// 54：隐身
fun SkillActionDetail.stealth(): String {
    val time = getTimeText(1, actionValue1)
    return getString(R.string.skill_action_type_desc_54, time)
}