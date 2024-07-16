package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString

// 44：进场等待
fun SkillActionDetail.waveStart() =
    getString(R.string.skill_action_type_desc_44, actionValue1.toInt())
