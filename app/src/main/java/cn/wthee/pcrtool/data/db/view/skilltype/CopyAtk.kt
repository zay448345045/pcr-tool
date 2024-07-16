package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget

// 103：复制攻击力
fun SkillActionDetail.copyAtk() = getString(
    R.string.skill_action_type_desc_103,
    actionDetail2 % 100,
    getTarget()
)