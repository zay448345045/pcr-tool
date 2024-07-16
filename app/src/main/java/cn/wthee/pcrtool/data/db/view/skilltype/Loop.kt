package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getString

// 63: 循环动作
fun SkillActionDetail.loop(): String {
    val successClause = if (actionDetail2 != 0)
        getString(R.string.skill_action_type_desc_63_success, actionDetail2 % 100)
    else
        UNKNOWN
    val failureClause = if (actionDetail3 != 0)
        getString(R.string.skill_action_type_desc_63_failure, actionDetail3 % 100)
    else
        UNKNOWN
    val main = getString(
        R.string.skill_action_type_desc_63,
        actionValue2.toString(),
        actionDetail1 % 100,
        actionValue1.toString(),
        actionValue3.toString()
    )

    return main + if (successClause != UNKNOWN && failureClause != UNKNOWN) {
        successClause + failureClause
    } else if (successClause != UNKNOWN) {
        successClause
    } else if (failureClause != UNKNOWN) {
        failureClause
    } else {
        UNKNOWN
    }
}