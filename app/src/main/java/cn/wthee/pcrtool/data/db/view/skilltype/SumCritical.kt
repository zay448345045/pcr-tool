package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString

//107：暴击率合计
fun SkillActionDetail.sumCritical() =
    getString(R.string.skill_action_type_desc_107, actionDetail1 % 100)