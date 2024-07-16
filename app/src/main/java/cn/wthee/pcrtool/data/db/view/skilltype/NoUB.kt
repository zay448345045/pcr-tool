package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString

// 29：无法使用 UB
fun SkillActionDetail.noUB() = getString(R.string.skill_action_type_desc_29)