package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString

// 901：战斗开始时生效
fun SkillActionDetail.exEquipFull() = getString(R.string.skill_action_type_desc_901)

// 902：45秒
fun SkillActionDetail.exEquipHalf() =
    getString(R.string.skill_action_type_desc_902, actionValue3.toInt())