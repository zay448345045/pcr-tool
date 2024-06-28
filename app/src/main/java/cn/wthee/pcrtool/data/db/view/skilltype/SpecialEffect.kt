package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget

// 94：技能特效
fun SkillActionDetail.specialEffect() = getString(R.string.skill_action_type_desc_94, getTarget())