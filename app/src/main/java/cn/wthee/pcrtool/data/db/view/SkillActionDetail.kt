package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.SkillActionType
import cn.wthee.pcrtool.data.enums.getAilment
import cn.wthee.pcrtool.data.enums.toSkillActionType
import cn.wthee.pcrtool.data.model.SkillActionText
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.UNKNOWN
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.getAtkType
import cn.wthee.pcrtool.utils.getAura
import cn.wthee.pcrtool.utils.getBarrierType
import cn.wthee.pcrtool.utils.getPercent
import cn.wthee.pcrtool.utils.getStatus
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getTargetType
import cn.wthee.pcrtool.utils.getTimeText
import cn.wthee.pcrtool.utils.getValueText
import cn.wthee.pcrtool.utils.initOtherLimit
import kotlin.math.abs
import kotlin.math.max


/**
 * 技能效果
 */
@Suppress("RegExpRedundantEscape")
data class SkillActionDetail(
    @PrimaryKey
    @ColumnInfo(name = "lv") var level: Int = 0,
    @ColumnInfo(name = "atk") var atk: Int = 0,
    @ColumnInfo(name = "action_id") var actionId: Int = 0,
    @ColumnInfo(name = "class_id") var classId: Int = 0,
    @ColumnInfo(name = "action_type") var actionType: Int = 0,
    @ColumnInfo(name = "action_detail_1") var actionDetail1: Int = 0,
    @ColumnInfo(name = "action_detail_2") var actionDetail2: Int = 0,
    @ColumnInfo(name = "action_detail_3") var actionDetail3: Int = 0,
    @ColumnInfo(name = "action_value_1") var actionValue1: Double = 0.0,
    @ColumnInfo(name = "action_value_2") var actionValue2: Double = 0.0,
    @ColumnInfo(name = "action_value_3") var actionValue3: Double = 0.0,
    @ColumnInfo(name = "action_value_4") var actionValue4: Double = 0.0,
    @ColumnInfo(name = "action_value_5") var actionValue5: Double = 0.0,
    @ColumnInfo(name = "action_value_6") var actionValue6: Double = 0.0,
    @ColumnInfo(name = "action_value_7") var actionValue7: Double = 0.0,
    @ColumnInfo(name = "target_assignment") var targetAssignment: Int = 0,
    @ColumnInfo(name = "target_area") var targetArea: Int = 0,
    @ColumnInfo(name = "target_range") var targetRange: Int = 0,
    @ColumnInfo(name = "target_type") var targetType: Int = 0,
    @ColumnInfo(name = "target_number") var targetNumber: Int = 0,
    @ColumnInfo(name = "target_count") var targetCount: Int = 0,
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "ailment_name") var tag: String = "",
    @ColumnInfo(name = "isRfSkill") var isRfSkill: Boolean = false,
    @ColumnInfo(name = "isOtherRfSkill") var isOtherRfSkill: Boolean = false,
    @Ignore
    var dependId: Int = 0,
    @Ignore
    var isTpLimitAction: Boolean = false,
    @Ignore
    var isOtherLimitAction: Boolean = false
) {

    /**
     * 获取技能效果
     * 判断逻辑参考  MalitsPlus [https://github.com/MalitsPlus]
     */
    fun getActionDesc(): SkillActionText {
        //召唤物编号
        var summonUnitId = 0
        if (toSkillActionType(actionType) == SkillActionType.SUMMON) {
            summonUnitId = actionDetail2
        }
        //生成技能效果文本
        val formatDescText = try {
            formatDesc()
        } catch (e: Exception) {
            LogReportUtil.upload(e, getDebugText())
            UNKNOWN
        }
        //是否显示系数判断
        val showCoe = when (toSkillActionType(actionType)) {
            SkillActionType.ADDITIVE,
            SkillActionType.MULTIPLE,
            SkillActionType.DIVIDE,
            SkillActionType.RATE_DAMAGE -> true

            else -> false
        }
        val skillActionText = SkillActionText(
            actionId = actionId,
            tag = tag,
            actionDesc = "(${actionId % 100}) $formatDescText",
            summonUnitId = summonUnitId,
            showCoe = showCoe,
            level = level,
            atk = atk,
            isTpLimitAction = isTpLimitAction,
            isOtherLimitAction = isOtherLimitAction
        )
        if (BuildConfig.DEBUG) {
            skillActionText.debugText = getDebugText()
        }
        return skillActionText
    }

    /**
     * 调试用信息
     */
    private fun getDebugText() = """
                    action_id:${this.actionId}
                    action_type:${this.actionType}
                    detail:${this.actionDetail1}/${this.actionDetail2}/${this.actionDetail3}
                    value:${this.actionValue1}/${this.actionValue2}/${this.actionValue3}/${this.actionValue4}
                    /${this.actionValue5}/${this.actionValue6}/${this.actionValue7}
                    targetAssignment:${this.targetAssignment}
                    targetCount:${this.targetCount}
                    targetNumber:${this.targetNumber}
                    targetRange:${this.targetRange}
                    targetType:${this.targetType}
                    targetArea:${this.targetArea}
                """.trimIndent()

    /**
     * 获取描述详情
     */
    private fun formatDesc(): String {
        //设置状态标签
        val ailmentName = getAilment(actionType)
        if (ailmentName.isNotEmpty()) {
            tag = ailmentName
        }

        return when (toSkillActionType(actionType)) {
            // 1：造成伤害
            SkillActionType.DAMAGE -> {
                val atkType = getAtkType()

                //适应伤害类型
                val adaptive = getString(
                    when (actionDetail2) {
                        1 -> R.string.skill_adaptive_lower_defense
                        else -> R.string.none
                    }
                )

                //暴伤倍率
                val multipleDamage = if (actionValue6 > 0) {
                    val multiple = if (actionValue6 > 1) {
                        "[${actionValue6 * 2}]"
                    } else {
                        "[2]"
                    }
                    getString(R.string.skill_critical_damage_multiple, multiple)
                } else {
                    ""
                }

                //必定暴击
                val mustCritical = getString(
                    if (actionValue5.toInt() == 1) {
                        R.string.skill_must_critical
                    } else {
                        R.string.none
                    }
                )

                //无视防御，fixme 需优化逻辑，龙拳为0但需要显示，106501108 106501109
                val ignoreDef =
                    if (actionValue7 > 0 || actionId == 106501108 || actionId == 106501109) {
                        val def = " [${actionValue7.toInt()}] "
                        getString(R.string.skill_ignore_def, def)
                    } else {
                        ""
                    }

                val value =
                    getValueText(1, actionValue1, actionValue2, actionValue3, v4 = actionValue4)

                getString(
                    R.string.skill_action_type_desc_1,
                    getTarget(),
                    value,
                    atkType,
                    adaptive,
                    multipleDamage,
                    mustCritical,
                    ignoreDef
                )
            }
            // 2：位移
            SkillActionType.MOVE -> {

                val directionText = getString(
                    if (actionValue1 > 0) {
                        R.string.skill_forward
                    } else {
                        R.string.skill_backward
                    }
                )
                val positionText = getString(
                    if (actionValue1 > 0) {
                        R.string.skill_ahead
                    } else {
                        R.string.skill_rear
                    }
                )
                val moveText =
                    getString(
                        R.string.skill_move,
                        getTarget(),
                        positionText,
                        abs(actionValue1).toInt()
                    )
                val returnText = getString(R.string.skill_return)
                val speedText = getString(R.string.skill_move_speed, actionValue2.toInt())
                when (actionDetail1) {
                    //移动后返回
                    1 -> moveText + returnText
                    //前、后移动后返回
                    2 -> directionText + moveText + returnText
                    //移动
                    3 -> moveText
                    //方向
                    4, 7 -> directionText + moveText
                    //方向、速度
                    5 -> moveText + positionText + speedText
                    6 -> directionText + moveText + speedText
                    else -> UNKNOWN
                }

            }
            // 3：改变对方位置
            SkillActionType.CHANGE_ENEMY_POSITION -> {
                when (actionDetail1) {
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
            }
            // 4：回复 HP
            SkillActionType.HEAL -> {
                val value = getValueText(2, actionValue2, actionValue3, actionValue4)
                getString(R.string.skill_action_type_desc_4, getTarget(), value)
            }
            // 5：回复 HP
            SkillActionType.CURE -> UNKNOWN
            // 6：护盾
            SkillActionType.BARRIER -> {
                val value = getValueText(1, actionValue1, actionValue2)
                val time = getTimeText(3, actionValue3, actionValue4)
                val type = getBarrierType(actionDetail1)
                if (type != UNKNOWN) {
                    getString(R.string.skill_action_type_desc_6, getTarget(), type, value, time)
                } else {
                    type
                }
            }
            // 7：指定攻击对象
            SkillActionType.CHOOSE_ENEMY -> {
                getString(R.string.skill_action_type_desc_7, getTarget())
            }
            // 8：行动速度变更、83：可叠加行动速度变更、99：范围速度变更
            SkillActionType.CHANGE_ACTION_SPEED, SkillActionType.SUPERIMPOSE_CHANGE_ACTION_SPEED, SkillActionType.SPEED_FIELD -> {
                //判断异常状态
                tag = getString(
                    when (actionDetail1) {
                        1 -> R.string.skill_ailment_1
                        2 -> R.string.skill_ailment_2
                        3 -> R.string.skill_ailment_3
                        4 -> R.string.skill_ailment_4
                        5 -> R.string.skill_ailment_5
                        6 -> R.string.skill_ailment_6
                        7, 12, 14 -> R.string.skill_ailment_7_12_14
                        8 -> R.string.skill_ailment_8
                        9 -> R.string.skill_ailment_9
                        10 -> R.string.skill_ailment_10
                        11 -> R.string.skill_ailment_11
                        13 -> R.string.skill_ailment_13
                        else -> R.string.unknown
                    }
                )
                val value = getValueText(1, actionValue1, actionValue2)
                val time = getTimeText(3, actionValue3, actionValue4)

                //额外、范围速度变更
                when (actionType) {
                    SkillActionType.SUPERIMPOSE_CHANGE_ACTION_SPEED.type -> {
                        tag += getString(R.string.skill_ailment_extra)
                    }

                    SkillActionType.SPEED_FIELD.type -> {
                        tag += getString(R.string.skill_ailment_field)
                    }
                }

                when (actionDetail1) {
                    1, 2 -> {
                        val descText =
                            if (actionType == SkillActionType.SUPERIMPOSE_CHANGE_ACTION_SPEED.type) {
                                val type = getString(
                                    if (actionDetail1 == 1) {
                                        R.string.skill_reduce
                                    } else {
                                        R.string.skill_increase
                                    }
                                )
                                getString(R.string.skill_action_speed_change, type, value)
                            } else {
                                getString(R.string.skill_action_speed_multiple, value)
                            }
                        if (actionType == SkillActionType.SPEED_FIELD.type) {
                            getString(
                                R.string.skill_action_type_desc_field,
                                actionValue5.toInt(),
                                descText,
                                time
                            )
                        } else {
                            "${tag}${getTarget()}，$descText$time"
                        }
                    }

                    else -> {
                        val count = if (actionDetail2 == 1) {
                            getString(R.string.skill_action_hit_remove)
                        } else {
                            ""
                        }
                        getString(R.string.skill_action_type_desc_8, getTarget(), tag, time, count)
                    }
                }
            }
            // 9：持续伤害
            SkillActionType.DOT -> {
                tag = getString(
                    when (actionDetail1) {
                        0 -> R.string.skill_dot_0
                        1, 7 -> R.string.skill_dot_1_7
                        2 -> R.string.skill_dot_2
                        3, 8 -> R.string.skill_dot_3_8
                        4 -> R.string.skill_dot_4
                        5 -> R.string.skill_dot_5
                        else -> R.string.unknown
                    }
                )
                val value = getValueText(1, actionValue1, actionValue2)
                val time = getTimeText(3, actionValue3, actionValue4)
                val dotIncrease = if (actionDetail1 == 5) {
                    getString(R.string.skill_action_dot_increase, actionValue5.toInt())
                } else {
                    ""
                }
                getString(
                    R.string.skill_action_type_desc_9,
                    getTarget(),
                    tag,
                    value,
                    dotIncrease,
                    time
                )
            }
            // 10：buff/debuff
            SkillActionType.AURA -> {
                tag = getString(
                    if (actionDetail1 % 10 == 0) {
                        R.string.skill_buff
                    } else {
                        R.string.skill_debuff
                    }
                )
                if (actionDetail1 % 1000 / 10 == 5) {
                    //回避等技能限制
                    initOtherLimit()
                }
                val value = getValueText(2, actionValue2, actionValue3, percent = getPercent())
                val aura = getAura(actionDetail1, value)
                val time = getTimeText(4, actionValue4, actionValue5)

                if (actionDetail2 == 2) {
                    getString(R.string.skill_action_type_desc_10_break, getTarget(), aura)
                } else {
                    "${getTarget()}${aura}$time"
                }
            }
            //11：魅惑/混乱12：黑暗 13：沉默
            SkillActionType.CHARM, SkillActionType.BLIND, SkillActionType.SILENCE -> {
                val chance =
                    getValueText(
                        3,
                        actionValue3,
                        if (actionValue3.toInt() == 100) 0.0 else 1.0,
                        0.0,
                        percent = "%"
                    )
                val time = getTimeText(1, actionValue1, actionValue2)
                if (toSkillActionType(actionType) == SkillActionType.CHARM) {
                    tag = getString(
                        when (actionDetail1) {
                            0 -> R.string.skill_charm_0
                            1 -> R.string.skill_charm_1
                            else -> R.string.unknown
                        }
                    )
                }

                getString(
                    R.string.skill_action_type_desc_12_13,
                    chance,
                    getTarget(),
                    tag,
                    time
                ) + if (actionType == 12) {
                    getString(R.string.skill_action_atk_miss, 100 - actionDetail1)
                } else {
                    ""
                }
            }
            // 14：行动模式变更
            SkillActionType.CHANGE_MODE -> {
                when (actionDetail1) {
                    1 -> getString(
                        R.string.skill_action_loop_change,
                        getTimeText(1, actionValue1)
                    )

                    2 -> getString(R.string.skill_action_type_desc_14_2, actionValue1.toInt())
                    3 -> getString(R.string.skill_action_type_desc_14_3)
                    else -> UNKNOWN
                }
            }
            // 15：召唤
            SkillActionType.SUMMON -> {
                val desc = getString(R.string.skill_action_summon_unit)
                when {
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
            // 16：TP 相关
            SkillActionType.CHANGE_TP -> {
                //tp技能限制
                if (level > Constants.TP_LIMIT_LEVEL && isRfSkill) {
                    isTpLimitAction = true
                }
                val value = getValueText(1, actionValue1, actionValue2)
                tag = getString(
                    when (actionDetail1) {
                        1, 4 -> R.string.skill_action_tp_recovery
                        else -> R.string.skill_action_tp_reduce
                    }
                )
                "${getTarget()}${tag} $value"
            }
            // 17：触发条件
            SkillActionType.TRIGGER -> {
                val desc = when (actionDetail1) {
                    2 -> getString(R.string.skill_action_type_desc_17_2, actionValue1.toInt())
                    3 -> getString(R.string.skill_action_type_desc_17_3, actionValue3.toInt())
                    4 -> getString(R.string.skill_action_type_desc_17_4, actionValue1.toInt())
                    5 -> getString(R.string.skill_action_type_desc_17_5, actionValue1.toInt())
                    7 -> getString(R.string.skill_action_type_desc_17_7, actionValue3.toInt())
                    8 -> getString(R.string.skill_action_type_desc_17_8, actionValue1.toInt())
                    9 -> getString(
                        R.string.skill_action_type_desc_17_9,
                        actionValue1.toInt(),
                        getTimeText(3, actionValue3)
                    )

                    10 -> getString(R.string.skill_action_type_desc_17_10, actionValue1.toInt())
                    11 -> getString(R.string.skill_action_type_desc_17_11)
                    13 -> {
                        getString(R.string.skill_action_type_desc_17_13, actionValue3.toInt())
                    }

                    else -> UNKNOWN
                }

                getString(R.string.skill_action_condition, desc)
            }
            // 18：蓄力、19：伤害充能
            SkillActionType.CHARGE, SkillActionType.DAMAGE_CHARGE -> {
                val desc = getString(
                    R.string.skill_action_type_desc_18_19,
                    actionValue3.toString()
                )

                val extraDesc = if (actionValue1 != 0.0 || actionValue2 != 0.0) {
                    val value = getValueText(1, actionValue1, actionValue2)
                    getString(
                        R.string.skill_action_type_desc_18_19_detail,
                        actionDetail2 % 10,
                        value
                    )
                } else {
                    ""
                }
                desc + extraDesc
            }
            // 20：挑衅
            SkillActionType.TAUNT -> {
                //回避等技能限制
                initOtherLimit()
                val time = getTimeText(1, actionValue1, actionValue2)
                getString(R.string.skill_action_type_desc_20, getTarget(), ailmentName, time)
            }
            // 21：回避
            SkillActionType.INVINCIBLE -> {
                //回避等技能限制
                initOtherLimit()
                tag = getString(
                    when (actionDetail1) {
                        1 -> R.string.skill_action_type_desc_21_1
                        2 -> R.string.skill_action_type_desc_21_2
                        3 -> R.string.skill_action_type_desc_21_3
                        else -> R.string.unknown
                    }
                )
                if (actionValue1 > 0) {
                    val time = getTimeText(1, actionValue1, actionValue2)
                    "$tag$time"
                } else {
                    tag
                }
            }
            // 22：改变模式
            SkillActionType.CHANGE_PATTERN -> {
                when (actionDetail1) {
                    1 -> getString(
                        R.string.skill_action_loop_change,
                        if (actionValue1 > 0) {
                            getTimeText(1, actionValue1)
                        } else {
                            ""
                        }
                    )

                    2 -> getString(
                        R.string.skill_action_skill_anim_change,
                        getTimeText(1, actionValue1)
                    )

                    else -> UNKNOWN
                }
            }
            // 23：判定对象状态
            SkillActionType.IF_STATUS -> {
                val status = getStatus(actionDetail1)
                var trueClause = UNKNOWN
                var falseClause = UNKNOWN
                if (actionDetail2 != 0) {
                    trueClause = if (status != UNKNOWN) {
                        getString(
                            R.string.skill_action_if_status,
                            getTarget(),
                            status,
                            actionDetail2 % 10
                        )
                    } else {
                        when (actionDetail1) {
                            in 600..699, 710, 6145 -> {
                                getString(
                                    R.string.skill_action_if_mark,
                                    getTarget(),
                                    actionDetail2 % 10
                                )
                            }

                            700 -> {
                                getString(
                                    R.string.skill_action_if_alone,
                                    getTarget(),
                                    actionDetail2 % 10
                                )
                            }

                            in 901..999 -> {
                                getString(
                                    R.string.skill_action_if_hp_below,
                                    getTarget(),
                                    actionDetail1 - 900,
                                    actionDetail2 % 10
                                )
                            }

                            2000, 1300 -> {
                                getString(
                                    R.string.skill_action_if_unit_atk_type,
                                    getTarget(),
                                    getString(R.string.skill_status_physical_atk),
                                    actionDetail2 % 10
                                )
                            }

                            2001 -> {
                                getString(
                                    R.string.skill_action_if_unit_atk_type,
                                    getTarget(),
                                    getString(R.string.skill_status_magic_atk),
                                    actionDetail2 % 10
                                )
                            }

                            else -> UNKNOWN
                        }
                    }
                }
                if (actionDetail3 != 0) {
                    falseClause = if (status != UNKNOWN) {
                        getString(
                            R.string.skill_action_if_status_not,
                            getTarget(),
                            status,
                            actionDetail3 % 10
                        )
                    } else {
                        when (actionDetail1) {
                            in 600..699, 710, 6145 -> {
                                getString(
                                    R.string.skill_action_if_mark_not,
                                    getTarget(),
                                    actionDetail3 % 10
                                )
                            }

                            700 -> {
                                getString(
                                    R.string.skill_action_if_alone_not,
                                    getTarget(),
                                    actionDetail3 % 10
                                )
                            }

                            in 901..999 -> {
                                getString(
                                    R.string.skill_action_if_hp_above,
                                    getTarget(),
                                    actionDetail1 - 900,
                                    actionDetail3 % 10
                                )
                            }

                            2000, 1300 -> {
                                getString(
                                    R.string.skill_action_if_unit_atk_type,
                                    getTarget(),
                                    getString(R.string.skill_status_magic_atk),
                                    actionDetail3 % 10
                                )
                            }

                            2001 -> {
                                getString(
                                    R.string.skill_action_if_unit_atk_type,
                                    getTarget(),
                                    getString(R.string.skill_status_physical_atk),
                                    actionDetail3 % 10
                                )
                            }

                            else -> UNKNOWN
                        }
                    }
                }
                //条件
                if (actionDetail1 in 0..99) {
                    when {
                        actionDetail2 != 0 && actionDetail3 != 0 -> {
                            getString(
                                R.string.skill_action_random_1,
                                actionDetail1,
                                actionDetail2 % 10,
                                actionDetail3 % 10
                            )
                        }

                        actionDetail2 != 0 -> {
                            getString(
                                R.string.skill_action_random_2,
                                actionDetail1,
                                actionDetail2 % 10
                            )
                        }

                        actionDetail3 != 0 -> {
                            getString(
                                R.string.skill_action_random_2,
                                100 - actionDetail1,
                                actionDetail3 % 10
                            )
                        }

                        else -> UNKNOWN
                    }
                } else {
                    when {
                        trueClause != UNKNOWN && falseClause != UNKNOWN -> {
                            getString(
                                R.string.skill_action_condition,
                                "${trueClause}；${falseClause}"
                            )
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
            }
            // 24：复活
            SkillActionType.REVIVAL -> {
                getString(
                    R.string.skill_action_type_desc_24,
                    getTarget(),
                    (actionValue2 * 100).toInt()
                )
            }
            // 25：连续攻击
            SkillActionType.CONTINUOUS_ATTACK -> UNKNOWN
            // 26：系数提升
            SkillActionType.ADDITIVE, SkillActionType.MULTIPLE, SkillActionType.DIVIDE -> {
                val attrType = when (actionValue1.toInt()) {
                    7 -> getString(R.string.skill_physical_str)
                    8 -> getString(R.string.skill_magic_str)
                    9 -> getString(R.string.skill_physical_def)
                    10 -> getString(R.string.skill_magic_def)
                    else -> UNKNOWN
                }
                val changeType = when (toSkillActionType(actionType)) {
                    SkillActionType.ADDITIVE -> getString(R.string.skill_action_type_desc_additive)
                    SkillActionType.MULTIPLE -> getString(R.string.skill_action_type_desc_multiple)
                    SkillActionType.DIVIDE -> getString(R.string.skill_action_type_desc_divide)
                    else -> UNKNOWN
                }
                val value = getValueText(2, actionValue2, actionValue3, hideIndex = true)
                val commonDesc = getString(
                    R.string.skill_action_change_coe,
                    actionDetail1 % 10,
                    actionDetail2,
                    changeType,
                    value
                )

                val extraDesc = when (actionValue1.toInt()) {
                    2 -> {
                        val mValue = when {
                            actionDetail3 == 0 -> {
                                "[${actionValue2}]"
                            }

                            actionDetail2 == 0 -> {
                                "[${actionValue3}]"
                            }

                            else -> {
                                "[${(actionValue2 + 2 * actionValue3 * level)}] <$actionValue2 + ${2 * actionValue3} * 技能等级> "
                            }
                        }
                        val mDesc = getString(
                            R.string.skill_action_change_coe,
                            actionDetail1 % 10,
                            actionDetail2,
                            changeType,
                            mValue
                        )
                        getString(R.string.skill_action_change_coe_2, mDesc)
                    }

                    0 -> getString(R.string.skill_action_change_coe_0, commonDesc)
                    1 -> getString(R.string.skill_action_change_coe_1, commonDesc)
                    4 -> {
                        val targetStr = getTargetType()
                        val targetType = if (targetStr != "") {
                            targetStr
                        } else {
                            getString(R.string.skill_target_none)
                        }
                        getString(R.string.skill_action_change_coe_4, commonDesc, targetType)

                    }

                    5 -> getString(R.string.skill_action_change_coe_5, commonDesc)
                    6 -> getString(R.string.skill_action_change_coe_6, commonDesc)
                    in 7..10 -> getString(
                        R.string.skill_action_change_coe_7_10,
                        commonDesc,
                        getTarget(),
                        attrType
                    )

                    12 -> getString(R.string.skill_action_change_coe_12, commonDesc, getTarget())
                    13 -> getString(R.string.skill_action_change_coe_13, commonDesc)
                    15 -> getString(R.string.skill_action_change_coe_15, commonDesc)
                    102 -> getString(R.string.skill_action_change_coe_102, commonDesc)
                    in 20 until 30 -> getString(
                        R.string.skill_action_change_coe_skill_count,
                        commonDesc
                    )

                    in 200 until 300,
                    in 2112 until 3000 -> getString(
                        R.string.skill_action_change_coe_mark_count,
                        commonDesc
                    )

                    else -> UNKNOWN
                }

                //上限判断
                if (actionValue4.toInt() != 0) {
                    val limitValue = getValueText(4, actionValue4, actionValue5, hideIndex = true)
                    val limit = getString(R.string.skill_action_limit, limitValue)
                    extraDesc + limit
                } else {
                    extraDesc
                }
            }
            // 28：特殊条件
            SkillActionType.IF_SP_STATUS -> {
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
                                    actionDetail2 % 10
                                )
                            }

                            599 -> {
                                getString(
                                    R.string.skill_action_sp_if_dot,
                                    getTarget(),
                                    actionDetail2 % 10
                                )
                            }

                            in 600..699,
                            in 6000..6999 -> {
                                getString(
                                    R.string.skill_action_sp_if_mark_count,
                                    getTarget(),
                                    max(actionValue3.toInt(), 1),
                                    actionDetail2 % 10
                                )
                            }

                            700 -> {
                                getString(
                                    R.string.skill_action_if_alone,
                                    getTarget(),
                                    actionDetail2 % 10
                                )
                            }

                            in 701..709 -> {
                                getString(
                                    R.string.skill_action_sp_if_unit_count,
                                    getTarget(),
                                    actionDetail1 - 700,
                                    actionDetail2 % 10
                                )
                            }

                            720 -> {
                                getString(
                                    R.string.skill_action_sp_if_unit_exist,
                                    getTarget(),
                                    actionDetail2 % 10
                                )
                            }

                            in 901..999 -> {
                                getString(
                                    R.string.skill_action_if_hp_below,
                                    getTarget(),
                                    actionDetail1 - 900,
                                    actionDetail2 % 10
                                )
                            }

                            1000 -> {
                                getString(R.string.skill_action_sp_if_kill, actionDetail2 % 10)
                            }

                            1001 -> {
                                getString(
                                    R.string.skill_action_sp_if_critical,
                                    actionDetail2 % 10
                                )
                            }

                            in 1200..1299 -> {
                                getString(
                                    R.string.skill_action_sp_if_skill_count,
                                    getTarget(),
                                    actionDetail1 % 10,
                                    actionDetail2 % 10
                                )
                            }

                            2000, 1300 -> {
                                getString(
                                    R.string.skill_action_if_unit_atk_type,
                                    getTarget(),
                                    getString(R.string.skill_status_physical_atk),
                                    actionDetail2 % 10
                                )
                            }

                            2001 -> {
                                getString(
                                    R.string.skill_action_if_unit_atk_type,
                                    getTarget(),
                                    getString(R.string.skill_status_magic_atk),
                                    actionDetail2 % 10
                                )
                            }

                            else -> getString(
                                R.string.skill_action_if_status,
                                getTarget(),
                                status,
                                actionDetail2 % 10
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
                                    actionDetail3 % 10
                                )
                            }

                            599 -> {
                                getString(
                                    R.string.skill_action_sp_if_dot_not,
                                    getTarget(),
                                    actionDetail3 % 10
                                )
                            }

                            in 600..699,
                            in 6000..6999 -> {
                                getString(
                                    R.string.skill_action_sp_if_mark_count_not,
                                    getTarget(),
                                    max(actionValue3.toInt(), 1),
                                    actionDetail3 % 10
                                )
                            }

                            700 -> {
                                getString(
                                    R.string.skill_action_if_alone_not,
                                    getTarget(),
                                    actionDetail3 % 10
                                )
                            }

                            in 701..709 -> {
                                getString(
                                    R.string.skill_action_sp_if_unit_count_not,
                                    getTarget(),
                                    actionDetail1 - 700,
                                    actionDetail3 % 10
                                )
                            }

                            720 -> {
                                getString(
                                    R.string.skill_action_sp_if_unit_exist_not,
                                    getTarget(),
                                    actionDetail3 % 10
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
                                getString(R.string.skill_action_sp_if_kill_not, actionDetail3 % 10)
                            }

                            1001 -> {
                                getString(
                                    R.string.skill_action_sp_if_critical_not,
                                    actionDetail3 % 10
                                )
                            }

                            in 1200..1299 -> {
                                getString(
                                    R.string.skill_action_sp_if_skill_count_not,
                                    getTarget(),
                                    actionDetail1 % 10,
                                    actionDetail3 % 10
                                )
                            }

                            2000, 1300 -> {
                                getString(
                                    R.string.skill_action_if_unit_atk_type,
                                    getTarget(),
                                    getString(R.string.skill_status_magic_atk),
                                    actionDetail3 % 10
                                )
                            }

                            2001 -> {
                                getString(
                                    R.string.skill_action_if_unit_atk_type,
                                    getTarget(),
                                    getString(R.string.skill_status_physical_atk),
                                    actionDetail3 % 10
                                )
                            }

                            else -> getString(
                                R.string.skill_action_if_status_not,
                                getTarget(),
                                status,
                                actionDetail3 % 10
                            )
                        }
                }

                //条件
                when {
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
            // 29：无法使用 UB
            SkillActionType.NO_UB -> getString(R.string.skill_action_type_desc_29)
            // 30：立即死亡
            SkillActionType.KILL_ME -> getString(R.string.skill_action_type_desc_30, getTarget())
            SkillActionType.CONTINUOUS_ATTACK_NEARBY -> UNKNOWN
            // 32：HP吸收
            SkillActionType.LIFE_STEAL -> {
                //回避等技能限制
                initOtherLimit()
                val value = getValueText(1, actionValue1, actionValue2)
                getString(
                    R.string.skill_action_type_desc_32,
                    getTarget(),
                    actionValue3.toInt(),
                    ailmentName,
                    value
                )
            }
            // 33：反伤
            SkillActionType.STRIKE_BACK -> {
                val value = getValueText(1, actionValue1, actionValue2)
                val type = getBarrierType(actionDetail1)
                val shieldText =
                    getString(R.string.skill_action_type_desc_6, getTarget(), type, "", "")
                val backType = when (actionDetail1) {
                    1, 3 -> getString(R.string.skill_physical)
                    2, 4 -> getString(R.string.skill_magic)
                    else -> ""
                }
                val hpRecovery = when (actionDetail1) {
                    3, 4, 6 -> getString(R.string.skill_action_type_desc_33_hp)
                    else -> ""
                }

                if (actionDetail1 <= 6) {
                    getString(
                        R.string.skill_action_type_desc_33,
                        shieldText,
                        backType,
                        value,
                        hpRecovery,
                        actionValue3.toInt()
                    )
                } else {
                    UNKNOWN
                }
            }
            // 34、102：伤害递增
            SkillActionType.ACCUMULATIVE_DAMAGE, SkillActionType.ACCUMULATIVE_DAMAGE_V2 -> {
                val value = getValueText(2, actionValue2, actionValue3)
                val limit =
                    getString(R.string.skill_action_limit_int, actionValue4.toInt())
                getString(R.string.skill_action_type_desc_34, value, limit)
            }
            // 35：特殊标记
            SkillActionType.SEAL -> {
                val count = abs(actionValue4.toInt())
                if (actionValue4.toInt() > 0) {
                    val time = getTimeText(3, actionValue3, hideIndex = true)
                    val limit =
                        getString(R.string.skill_action_limit_int, actionValue1.toInt())
                    getString(R.string.skill_action_type_desc_35, getTarget(), count, time, limit)
                } else {
                    getString(
                        R.string.skill_action_type_desc_35_reduce,
                        getTarget(),
                        count
                    )
                }
            }
            // 101：特殊标记v2
            SkillActionType.SEAL_V2 -> {
                val count = abs(actionDetail2)
                if (actionDetail2 >= 0) {
                    val time = getTimeText(3, actionValue3, hideIndex = true)
                    val limit = getString(R.string.skill_action_limit_int, actionValue1.toInt())
                    getString(R.string.skill_action_type_desc_101, getTarget(), count, time, limit)
                } else {
                    getString(
                        R.string.skill_action_type_desc_101_reduce,
                        getTarget(),
                        count
                    )
                }
            }
            // 36：攻击领域展开
            SkillActionType.ATTACK_FIELD -> {
                val atkType = getAtkType()
                val value = getValueText(
                    1,
                    actionValue1,
                    actionValue2,
                    actionValue3
                )
                val time = getTimeText(5, actionValue5, actionValue6)
                val damage = getString(R.string.skill_action_type_desc_36_damage, value, atkType)

                getString(
                    R.string.skill_action_type_desc_field,
                    actionValue7.toInt(),
                    damage,
                    time
                )
            }
            // 37：治疗领域展开
            SkillActionType.HEAL_FIELD -> {
                val value = getValueText(1, actionValue1, actionValue2, actionValue3)
                val heal = getString(R.string.skill_action_type_desc_37_heal, value)
                val time = getTimeText(5, actionValue5, actionValue6)

                getString(R.string.skill_action_type_desc_field, actionValue7.toInt(), heal, time)
            }
            // 38：buff/debuff领域展开
            SkillActionType.AURA_FIELD -> {
                val value = getValueText(1, actionValue1, actionValue2, percent = getPercent())
                val time = getTimeText(3, actionValue3, actionValue4)
                val aura = getAura(actionDetail1, value)

                getTarget() + getString(
                    R.string.skill_action_type_desc_field,
                    actionValue5.toInt(),
                    aura,
                    time
                )
            }
            // 39：持续伤害领域展开
            SkillActionType.DOT_FIELD -> {
                val time = getTimeText(1, actionValue1, actionValue2)
                val action =
                    getString(R.string.skill_action_type_desc_38_action, actionDetail1 % 10)

                getTarget() + getString(
                    R.string.skill_action_type_desc_field,
                    actionValue3.toInt(),
                    action,
                    time
                )
            }

            SkillActionType.CHANGE_ACTION_SPEED_FIELD -> UNKNOWN
            SkillActionType.CHANGE_UB_TIME -> UNKNOWN
            // 42：触发
            SkillActionType.LOOP_TRIGGER -> {
                when (actionDetail1) {
                    2 -> {
                        val value = getValueText(1, actionValue1, actionValue2, 0.0, percent = "%")

                        getString(
                            R.string.skill_action_type_desc_42_2,
                            actionValue4.toInt(),
                            value,
                            actionDetail2 % 10
                        )
                    }

                    14 -> {
                        val value = getValueText(1, actionValue1, actionValue2, 0.0, percent = "%")
                        var actionText = getString(id = R.string.skill_action_d, actionDetail2 % 10)
                        if (actionDetail3 != 0) {
                            actionText += "、" + getString(
                                id = R.string.skill_action_d,
                                actionDetail3 % 10
                            )
                        }
                        getString(
                            R.string.skill_action_type_desc_42_14,
                            actionValue4.toInt(),
                            value,
                            actionText
                        )
                    }

                    else -> UNKNOWN
                }
            }

            SkillActionType.IF_TARGETED -> UNKNOWN
            // 44：进场等待
            SkillActionType.WAVE_START -> {
                getString(R.string.skill_action_type_desc_44, actionValue1.toInt())
            }
            // 45：已使用技能数相关
            SkillActionType.SKILL_COUNT -> {
                val limit =
                    getString(R.string.skill_action_limit_int, actionValue1.toInt())
                getString(R.string.skill_action_type_desc_45, limit)
            }
            // 46：比例伤害
            SkillActionType.RATE_DAMAGE -> {
                val value = getValueText(1, actionValue1, actionValue2, percent = "%")
                when (actionDetail1) {
                    1 -> getString(R.string.skill_action_type_desc_46_1, getTarget(), value)
                    2 -> getString(R.string.skill_action_type_desc_46_2, getTarget(), value)
                    3 -> getString(R.string.skill_action_type_desc_46_3, getTarget(), value)
                    else -> UNKNOWN
                }
            }

            SkillActionType.UPPER_LIMIT_ATTACK -> {
                getString(R.string.skill_action_type_desc_47)
            }
            // 48：持续治疗
            SkillActionType.HOT -> {
                val type = when (actionDetail2) {
                    1 -> getString(R.string.attr_hp)
                    2 -> getString(R.string.attr_tp)
                    else -> UNKNOWN
                }
                val value = getValueText(1, actionValue1, actionValue2, actionValue3)
                val time = getTimeText(5, actionValue5, actionValue6)
                if (type != UNKNOWN) {
                    getString(R.string.skill_action_type_desc_48, getTarget(), type, value, time)
                } else {
                    UNKNOWN
                }
            }
            // 49：移除增益
            SkillActionType.DISPEL -> {
                val value = getValueText(1, actionValue1, actionValue2, 0.0, percent = "%")
                val type = when (actionDetail1) {
                    1, 3 -> getString(R.string.skill_buff)
                    2 -> getString(R.string.skill_debuff)
                    10 -> getString(R.string.skill_barrier)
                    else -> UNKNOWN
                }
                if (type != UNKNOWN) {
                    getString(R.string.skill_action_type_desc_49, value, getTarget(), type)
                } else {
                    UNKNOWN
                }
            }
            // 50：持续动作
            SkillActionType.CHANNEL -> {
                val time = getTimeText(4, actionValue4, actionValue5)
                val value = getValueText(2, actionValue2, actionValue3, percent = getPercent())
                val aura = getAura(actionDetail1, value)
                getString(
                    R.string.skill_action_type_desc_50,
                    getTarget(),
                    aura,
                    time,
                    actionDetail3
                )
            }
            // 52：改变单位距离
            SkillActionType.CHANGE_WIDTH -> {
                getString(R.string.skill_action_type_desc_52)
            }
            // 53：特殊状态：领域存在时；如：情姐
            SkillActionType.IF_HAS_FIELD -> {
                val content = if (actionDetail2 != 0 && actionDetail3 != 0) {
                    val otherwise =
                        getString(R.string.skill_action_type_desc_53_2, actionDetail3 % 10)
                    getString(R.string.skill_action_type_desc_53, actionDetail2 % 10, otherwise)
                } else if (actionDetail2 != 0) {
                    getString(R.string.skill_action_type_desc_53, actionDetail2 % 10, "")
                } else {
                    UNKNOWN
                }
                getString(R.string.skill_action_condition, content)
            }
            // 54：隐身
            SkillActionType.STEALTH -> {
                val time = getTimeText(1, actionValue1)
                getString(R.string.skill_action_type_desc_54, time)
            }
            // 55：部位移动
            SkillActionType.MOVE_PART -> {
                getString(
                    R.string.skill_action_type_desc_55,
                    actionValue4.toInt(),
                    -actionValue1.toInt()
                )
            }
            // 56：千里眼
            SkillActionType.COUNT_BLIND -> {
                val time = getTimeText(2, actionValue2, actionValue3)
                when (actionValue1.toInt()) {
                    1 -> getString(R.string.skill_action_type_desc_56_1, time)
                    2 -> {
                        val value = getValueText(2, actionValue2, actionValue3)
                        getString(R.string.skill_action_type_desc_56_2, getTarget(), value)
                    }

                    else -> UNKNOWN
                }
            }
            // 57：延迟攻击 如：万圣炸弹人的 UB
            SkillActionType.COUNT_DOWN -> {
                getString(
                    R.string.skill_action_type_desc_57,
                    getTarget(),
                    actionValue1.toInt(),
                    actionDetail1 % 10
                )
            }
            // 58：解除领域 如：晶姐 UB
            SkillActionType.STOP_FIELD -> {
                getString(
                    R.string.skill_action_type_desc_58,
                    actionDetail1 / 100 % 10,
                    actionDetail1 % 10
                )
            }
            // 59：回复妨碍
            SkillActionType.INHIBIT_HEAL_ACTION -> {
                val time = getTimeText(2, actionValue2)
                getString(
                    R.string.skill_action_type_desc_59,
                    getTarget(),
                    (actionValue1 * 100).toInt(),
                    time
                )
            }
            // 60：标记赋予
            SkillActionType.ATTACK_SEAL -> {
                val limit =
                    getString(R.string.skill_action_limit_int, actionValue1.toInt())
                val time = getTimeText(3, actionValue3, actionValue4)
                val target = getTarget()
                val desc = getString(R.string.skill_action_type_desc_60_0, time, limit)
                if (actionDetail1 == 3) {
                    getString(R.string.skill_action_type_desc_60_1, target, desc)
                } else if (actionDetail1 == 1 && actionDetail3 == 1) {
                    getString(R.string.skill_action_type_desc_60_2, target, desc)
                } else if (actionDetail1 == 4 && actionDetail3 == 1) {
                    getString(R.string.skill_action_type_desc_60_3, target, desc)
                } else if (actionDetail1 == 5 && actionDetail3 == 1) {
                    getString(R.string.skill_action_type_desc_60_4, target, desc)
                } else {
                    UNKNOWN
                }
            }
            // 61：恐慌
            SkillActionType.FEAR -> {
                val value = getValueText(3, actionValue3, actionValue4, 0.0, percent = "%")
                val time = getTimeText(1, actionValue1, actionValue2)
                getString(R.string.skill_action_type_desc_61, value, getTarget(), ailmentName, time)
            }
            // 62：畏惧
            SkillActionType.AWE -> {
                val value = getValueText(1, actionValue1, actionValue2, 0.0, percent = "%")
                val time = getTimeText(3, actionValue3, actionValue4)
                when (actionDetail1) {
                    0 -> getString(R.string.skill_action_type_desc_62_0, getTarget(), value, time)
                    1 -> getString(R.string.skill_action_type_desc_62_1, getTarget(), value, time)
                    else -> UNKNOWN
                }
            }
            // 63: 循环动作
            SkillActionType.LOOP -> {
                val successClause = if (actionDetail2 != 0)
                    getString(R.string.skill_action_type_desc_63_success, actionDetail2 % 10)
                else
                    UNKNOWN
                val failureClause = if (actionDetail3 != 0)
                    getString(R.string.skill_action_type_desc_63_failure, actionDetail3 % 10)
                else
                    UNKNOWN
                val main = getString(
                    R.string.skill_action_type_desc_63,
                    actionValue2.toString(),
                    actionDetail1 % 10,
                    actionValue1.toString(),
                    actionValue3.toString()
                )

                main + if (successClause != UNKNOWN && failureClause != UNKNOWN) {
                    successClause + failureClause
                } else if (successClause != UNKNOWN) {
                    successClause
                } else if (failureClause != UNKNOWN) {
                    failureClause
                } else {
                    UNKNOWN
                }
            }
            // 69：变身
            SkillActionType.REINDEER -> {
                val time = getTimeText(1, actionValue1, actionValue2)
                getString(R.string.skill_action_type_desc_69, getTarget(), time)
            }
            // 71：免死
            SkillActionType.EXEMPTION_DEATH -> {
                val value = getValueText(2, actionValue2, actionValue3, actionValue4)
                val time = getTimeText(6, actionValue6, actionValue7)
                getString(R.string.skill_action_type_desc_71, getTarget(), value, time)
            }
            // 72：伤害减免
            SkillActionType.DAMAGE_REDUCE -> {
                val type = when (actionDetail1) {
                    1 -> getString(R.string.skill_physical)
                    2 -> getString(R.string.skill_magic)
                    3 -> getString(R.string.skill_all)
                    else -> UNKNOWN
                }
                val value =
                    getValueText(1, actionValue1, actionValue2, percent = "%")
                val time = getTimeText(3, actionValue3, actionValue4)
                getString(R.string.skill_action_type_desc_72, getTarget(), type, value, time)
            }
            // 73：伤害护盾
            SkillActionType.LOG_BARRIER -> {
                val time = getTimeText(3, actionValue3, actionValue4)
                getString(
                    R.string.skill_action_type_desc_73,
                    getTarget(),
                    actionValue5.toInt(),
                    time
                )
            }
            // 75：次数触发
            SkillActionType.HIT_COUNT -> {
                val time = getTimeText(3, actionValue3, actionValue4)

                when (actionDetail1) {
                    3 -> getString(
                        R.string.skill_action_type_desc_75,
                        actionValue1.toInt(),
                        actionDetail2 % 10,
                        time
                    )

                    else -> UNKNOWN
                }
            }
            // 76：HP 回复量变化
            SkillActionType.HEAL_DOWN -> {
                val value =
                    getValueText(1, actionValue1, actionValue2, percent = getPercent())
                val time = getTimeText(3, actionValue3, actionValue4)
                getString(R.string.skill_action_type_desc_76, getTarget(), value, time)
            }
            // 77：被动叠加标记
            SkillActionType.IF_BUFF_SEAL -> {
                val time = getTimeText(3, actionValue3, actionValue4)
                val effect = when (actionDetail1) {
                    1 -> getString(R.string.skill_buff)
                    2 -> getString(R.string.skill_damage)
                    else -> UNKNOWN
                }
                val limit =
                    getString(R.string.skill_action_limit_int, actionValue1.toInt())

                getString(
                    R.string.skill_action_type_desc_77,
                    getTarget(),
                    effect,
                    actionDetail2,
                    time,
                    limit
                )
            }
            // 78：被击伤害上升
            SkillActionType.DMG_TAKEN_UP -> {
                val time = getTimeText(3, actionValue3, actionValue4)
                val limit = getString(R.string.skill_action_limit_int, actionValue2.toInt())
                //数量类型
                val countType = when (actionDetail1) {
                    1 -> getString(R.string.skill_action_type_desc_78_1)
                    else -> getString(R.string.unknown)
                }
                //增加或减少
                val effectType = when (actionDetail2) {
                    1 -> getString(R.string.skill_action_type_desc_additive)
                    2 -> getString(R.string.skill_action_type_desc_subtract)
                    else -> getString(R.string.unknown)
                }
                //倍数计算公式
                val valueText = "<${actionValue1} * ${countType}>"
                getString(
                    R.string.skill_action_type_desc_78,
                    getTarget(),
                    effectType,
                    valueText,
                    time,
                    limit
                )
            }

            // 79：行动时，造成伤害
            SkillActionType.ACTION_DOT -> {
                val value = getValueText(1, actionValue1, actionValue2, percent = getPercent())
                val time = getTimeText(3, actionValue3, actionValue4)
                val type: String
                val limit: String
                if (actionDetail1 == 10) {
                    type = getString(R.string.skill_hp_max)
                    limit = getString(R.string.skill_action_damage_limit_int, actionValue5.toInt())
                } else {
                    type = ""
                    limit = ""
                }
                getString(R.string.skill_action_type_desc_79, getTarget(), type, value, time, limit)
            }
            // 81：无效目标
            SkillActionType.NO_TARGET -> {
                getString(R.string.skill_action_type_desc_81, getTarget())
            }
            // 90：EX被动
            SkillActionType.EX -> {
                val type = when (actionDetail1) {
                    1 -> getString(R.string.attr_hp)
                    2 -> getString(R.string.attr_atk)
                    3 -> getString(R.string.attr_def)
                    4 -> getString(R.string.attr_magic_str)
                    5 -> getString(R.string.attr_magic_def)
                    6 -> getString(R.string.attr_physical_critical)
                    7 -> getString(R.string.attr_magic_critical)
                    else -> UNKNOWN
                }
                val value = getValueText(2, actionValue2, actionValue3)

                getString(R.string.skill_action_type_desc_90, type, value)
            }
            // 901：战斗开始时生效
            SkillActionType.EX_EQUIP -> {
                getString(R.string.skill_action_type_desc_901)
            }
            // 902：45秒
            SkillActionType.EX_EQUIP_HALF -> {
                getString(R.string.skill_action_type_desc_902, actionValue3.toInt())
            }
            // 92：改变 TP 获取倍率
            SkillActionType.CHANGE_TP_RATIO -> {
                getString(
                    R.string.skill_action_type_desc_92,
                    getTarget(),
                    actionValue1.toString()
                )
            }
            // 93：无视挑衅
            SkillActionType.IGNORE_TAUNT -> {
                getString(R.string.skill_action_type_desc_93, getTarget())
            }
            // 94：技能特效
            SkillActionType.SPECIAL_EFFECT -> {
                getString(R.string.skill_action_type_desc_94, getTarget())
            }
            // 95：隐匿
            SkillActionType.HIDE -> {
                val time = getTimeText(1, actionValue1, actionValue2)
                getString(R.string.skill_action_type_desc_95, getTarget(), time)
            }
            // 96：范围tp回复
            SkillActionType.TP_FIELD -> {
                val value = getValueText(1, actionValue1, actionValue2)
                val tp = getString(R.string.skill_action_type_desc_96_tp, value)
                val time = getTimeText(3, actionValue3, actionValue4)

                getString(R.string.skill_action_type_desc_field, actionValue5.toInt(), tp, time)
            }
            // 97：受击tp回复
            SkillActionType.TP_HIT -> {
                val limit =
                    getString(R.string.skill_action_limit_int, actionValue4.toInt())
                val time = getTimeText(5, actionValue5)
                val desc = getString(
                    R.string.skill_action_type_desc_35,
                    getTarget(),
                    actionValue3.toInt(),
                    time,
                    limit
                )
                val tpDesc = getString(
                    R.string.skill_action_type_desc_97,
                    actionValue1.toInt()
                )
                desc + tpDesc
            }
            // 98：改变 TP 减少时倍率
            SkillActionType.TP_HIT_REDUCE -> {
                val time = getTimeText(2, actionValue2, actionValue3)
                getString(
                    R.string.skill_action_type_desc_98,
                    getTarget(),
                    actionValue1.toString(),
                    time
                )
            }
            // 100：免疫无法行动的异常状态
            SkillActionType.IGNORE_SPEED_DOWN -> {
                val time = getTimeText(3, actionValue3)
                val limit = if (actionValue1.toInt() == -1) {
                    getString(R.string.none)
                } else {
                    getString(R.string.skill_action_type_desc_100_count, actionValue1.toInt())
                }
                getString(
                    R.string.skill_action_type_desc_100,
                    getTarget(),
                    limit,
                    time
                )
            }
            // 103：复制攻击力
            SkillActionType.COPY_ATK -> {
                getString(
                    R.string.skill_action_type_desc_103,
                    actionDetail2 % 10,
                    getTarget()
                )
            }

            // 105：环境效果
            SkillActionType.ENVIRONMENT -> {
                val type = when (actionDetail2) {
                    137 -> getString(R.string.skill_status_3137)
                    else -> getString(R.string.unknown)
                }
                val time = getTimeText(1, actionValue1)
                getString(R.string.skill_action_type_desc_105, type, time)
            }

            // 106：守护
            SkillActionType.GUARD -> {
                val type = when (actionDetail1) {
                    141 -> getString(R.string.skill_action_type_desc_106_type_141)
                    else -> getString(R.string.unknown)
                }
                val time = getTimeText(3, actionValue3, actionValue4)
                getString(R.string.skill_action_type_desc_106, getTarget(), type, time)
            }
            //107：暴击率合计
            SkillActionType.SUM_CRITICAL -> {
                getString(R.string.skill_action_type_desc_107, actionDetail1 % 10)
            }

            else -> {
                val value = getValueText(
                    1,
                    actionValue1,
                    actionValue2,
                    percent = getPercent()
                )
                val time = getTimeText(3, actionValue3, actionValue4)

                getString(
                    R.string.skill_action_type_unknown,
                    UNKNOWN,
                    getTarget(),
                    actionType,
                    value,
                    time
                )
            }
        }
    }


    constructor() : this(
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0.0,
        0,
        0,
        0,
        0,
        0,
        0,
        "",
        "0",
        false,
        false,
        0,
        false
    )
}