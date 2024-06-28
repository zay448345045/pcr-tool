package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.initOtherLimit

/**
 * 20：挑衅
 * @param ailmentName 异常状态名
 */
fun SkillActionDetail.taunt(ailmentName: String): String {
    //回避等技能限制
    initOtherLimit()
    val time = getTimeText(1, actionValue1, actionValue2)
    return getString(R.string.skill_action_type_desc_20, getTarget(), ailmentName, time)
}

// 93：无视挑衅
fun SkillActionDetail.ignoreTaunt() = getString(R.string.skill_action_type_desc_93, getTarget())
