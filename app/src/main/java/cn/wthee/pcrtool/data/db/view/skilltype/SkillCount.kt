package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString

// 45：已使用技能数相关
fun SkillActionDetail.skillCount(): String {
    val limit =
        getString(R.string.skill_action_limit_int, actionValue1.toInt())
    return getString(R.string.skill_action_type_desc_45, limit)
}
