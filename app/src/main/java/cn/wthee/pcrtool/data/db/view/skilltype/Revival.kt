package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget

// 24：复活
fun SkillActionDetail.revival() = getString(
    R.string.skill_action_type_desc_24,
    getTarget(),
    (actionValue2 * 100).toInt()
)