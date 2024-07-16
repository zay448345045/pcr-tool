package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget

// 81：无效目标
fun SkillActionDetail.noTarget() = getString(R.string.skill_action_type_desc_81, getTarget())