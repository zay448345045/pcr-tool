package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText

// 95：隐匿
fun SkillActionDetail.hide(): String {
    val time = getTimeText(1, actionValue1, actionValue2)
    return getString(R.string.skill_action_type_desc_95, getTarget(), time)
}