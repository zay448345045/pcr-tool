package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import kotlin.math.abs

// 15：召唤
fun SkillActionDetail.summon(): String {
    val desc = getString(R.string.skill_action_summon_unit)
    return when {
        actionValue7 > 0 -> {
            getString(
                R.string.skill_action_type_desc_15,
                getTarget(),
                getString(R.string.skill_ahead),
                actionValue7.toInt(),
                desc
            )
        }

        actionValue7 < 0 -> {
            getString(
                R.string.skill_action_type_desc_15,
                getTarget(),
                getString(R.string.skill_rear),
                abs(actionValue7).toInt(),
                desc
            )
        }

        else -> {
            getString(R.string.skill_action_summon_target, getTarget(), desc)
        }
    }
}