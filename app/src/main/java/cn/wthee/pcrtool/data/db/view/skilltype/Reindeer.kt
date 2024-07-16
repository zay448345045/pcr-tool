package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText

// 69：变身
fun SkillActionDetail.reindeer(): String {
    val time = getTimeText(1, actionValue1, actionValue2)
    return getString(R.string.skill_action_type_desc_69, getTarget(), time)
}