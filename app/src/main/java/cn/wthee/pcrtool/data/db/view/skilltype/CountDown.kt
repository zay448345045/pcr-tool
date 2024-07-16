package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget

// 57：延迟攻击 如：万圣炸弹人的 UB
fun SkillActionDetail.countDown() = getString(
    R.string.skill_action_type_desc_57,
    getTarget(),
    actionValue1.toInt(),
    actionDetail1 % 100
)