package cn.wthee.pcrtool.data.model


/**
 * 简化技能效果数据
 */
data class SkillActionText(
    val actionId: Int,
    val tag: String,
    var actionDesc: String,
    val summonUnitId: Int,
    val showCoe: Boolean,
    val level: Int,
    val atk: Int,
    val isTpLimitAction: Boolean,
    val isOtherLimitAction: Boolean,
) {
    var debugText = ""
}