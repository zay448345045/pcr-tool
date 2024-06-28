package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getValueText
import cn.wthee.pcrtool.utils.initOtherLimit

// 32：HP吸收
fun SkillActionDetail.lifeSteal(ailmentName: String): String {
    //回避等技能限制
    initOtherLimit()
    val value = getValueText(1, actionValue1, actionValue2)
    return getString(
        R.string.skill_action_type_desc_32,
        getTarget(),
        actionValue3.toInt(),
        ailmentName,
        value
    )
}