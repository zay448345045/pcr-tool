package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.getStatus
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import kotlin.math.max

// 28：特殊条件
fun SkillActionDetail.ifSpStatus(): String {
    val status = getStatus(actionDetail1)
    var trueClause = UNKNOWN
    var falseClause = UNKNOWN
    if (actionDetail2 != 0 || actionDetail3 == 0) {
        trueClause =
            when (actionDetail1) {
                in 0..99 -> {
                    getString(
                        R.string.skill_action_sp_if_rate,
                        actionDetail1,
                        actionDetail2 % 100
                    )
                }

                599 -> {
                    getString(
                        R.string.skill_action_sp_if_dot,
                        getTarget(),
                        actionDetail2 % 100
                    )
                }

                in 600..699,
                in 6000..6999 -> {
                    getString(
                        R.string.skill_action_sp_if_mark_count,
                        getTarget(),
                        max(actionValue3.toInt(), 1),
                        actionDetail2 % 100
                    )
                }

                700 -> {
                    getString(
                        R.string.skill_action_if_alone,
                        getTarget(),
                        actionDetail2 % 100
                    )
                }

                in 701..709 -> {
                    getString(
                        R.string.skill_action_sp_if_unit_count,
                        getTarget(),
                        actionDetail1 - 700,
                        actionDetail2 % 100
                    )
                }

                720 -> {
                    getString(
                        R.string.skill_action_sp_if_unit_exist,
                        getTarget(),
                        actionDetail2 % 100
                    )
                }

                in 901..999 -> {
                    getString(
                        R.string.skill_action_if_hp_below,
                        getTarget(),
                        actionDetail1 - 900,
                        actionDetail2 % 100
                    )
                }

                1000 -> {
                    getString(R.string.skill_action_sp_if_kill, actionDetail2 % 100)
                }

                1001 -> {
                    getString(
                        R.string.skill_action_sp_if_critical,
                        actionDetail2 % 100
                    )
                }

                in 1200..1299 -> {
                    getString(
                        R.string.skill_action_sp_if_skill_count,
                        getTarget(),
                        actionDetail1 % 10,
                        actionDetail2 % 100
                    )
                }

                2000, 1300 -> {
                    getString(
                        R.string.skill_action_if_unit_atk_type,
                        getTarget(),
                        getString(R.string.skill_status_physical_atk),
                        actionDetail2 % 100
                    )
                }

                2001 -> {
                    getString(
                        R.string.skill_action_if_unit_atk_type,
                        getTarget(),
                        getString(R.string.skill_status_magic_atk),
                        actionDetail2 % 100
                    )
                }

                else -> getString(
                    R.string.skill_action_if_status,
                    getTarget(),
                    status,
                    actionDetail2 % 100
                )
            }
    }

    if (actionDetail3 != 0) {
        falseClause =
            when (actionDetail1) {
                in 0..99 -> {
                    getString(
                        R.string.skill_action_sp_if_rate,
                        100 - actionDetail1,
                        actionDetail3 % 100
                    )
                }

                599 -> {
                    getString(
                        R.string.skill_action_sp_if_dot_not,
                        getTarget(),
                        actionDetail3 % 100
                    )
                }

                in 600..699,
                in 6000..6999 -> {
                    getString(
                        R.string.skill_action_sp_if_mark_count_not,
                        getTarget(),
                        max(actionValue3.toInt(), 1),
                        actionDetail3 % 100
                    )
                }

                700 -> {
                    getString(
                        R.string.skill_action_if_alone_not,
                        getTarget(),
                        actionDetail3 % 100
                    )
                }

                in 701..709 -> {
                    getString(
                        R.string.skill_action_sp_if_unit_count_not,
                        getTarget(),
                        actionDetail1 - 700,
                        actionDetail3 % 100
                    )
                }

                720 -> {
                    getString(
                        R.string.skill_action_sp_if_unit_exist_not,
                        getTarget(),
                        actionDetail3 % 100
                    )
                }

                in 901..999 -> {
                    getString(
                        R.string.skill_action_if_hp_above,
                        getTarget(),
                        actionDetail1 - 900,
                        actionDetail3 % 100
                    )
                }

                1000 -> {
                    getString(R.string.skill_action_sp_if_kill_not, actionDetail3 % 100)
                }

                1001 -> {
                    getString(
                        R.string.skill_action_sp_if_critical_not,
                        actionDetail3 % 100
                    )
                }

                in 1200..1299 -> {
                    getString(
                        R.string.skill_action_sp_if_skill_count_not,
                        getTarget(),
                        actionDetail1 % 10,
                        actionDetail3 % 100
                    )
                }

                2000, 1300 -> {
                    getString(
                        R.string.skill_action_if_unit_atk_type,
                        getTarget(),
                        getString(R.string.skill_status_magic_atk),
                        actionDetail3 % 100
                    )
                }

                2001 -> {
                    getString(
                        R.string.skill_action_if_unit_atk_type,
                        getTarget(),
                        getString(R.string.skill_status_physical_atk),
                        actionDetail3 % 100
                    )
                }

                else -> getString(
                    R.string.skill_action_if_status_not,
                    getTarget(),
                    status,
                    actionDetail3 % 100
                )
            }
    }

    //条件
    return when {
        trueClause != UNKNOWN && falseClause != UNKNOWN -> {
            getString(R.string.skill_action_condition, "${trueClause}；${falseClause}")
        }

        trueClause != UNKNOWN -> {
            getString(R.string.skill_action_condition, trueClause)
        }

        falseClause != UNKNOWN -> {
            getString(R.string.skill_action_condition, falseClause)
        }

        else -> UNKNOWN
    }
}