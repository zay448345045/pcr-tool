package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import kotlin.math.abs

// 3：改变对方位置
fun SkillActionDetail.changePosition() = when (actionDetail1) {
    1, 9 -> {
        tag = getString(R.string.skill_hit_up)
        getString(
            R.string.skill_action_type_desc_3_up,
            tag,
            getTarget(),
            (abs(actionValue1)).toInt()
        )
    }

    3, 6 -> {
        tag = getString(
            if (actionValue1 > 0) {
                R.string.skill_push
            } else {
                R.string.skill_pull
            }
        )
        getString(
            R.string.skill_action_type_desc_3_move,
            tag,
            getTarget(),
            (abs(actionValue1)).toInt()
        )
    }

    8 -> {
        tag = getString(R.string.skill_pull)
        getString(
            R.string.skill_action_type_desc_3_pull,
            getTarget(),
            tag,
            actionValue1.toInt()
        )
    }

    else -> UNKNOWN
}