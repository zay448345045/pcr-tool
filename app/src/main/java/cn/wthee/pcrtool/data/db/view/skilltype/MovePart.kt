package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString

// 55：部位移动
fun SkillActionDetail.movePart() = getString(
    R.string.skill_action_type_desc_55,
    actionValue4.toInt(),
    -actionValue1.toInt()
)