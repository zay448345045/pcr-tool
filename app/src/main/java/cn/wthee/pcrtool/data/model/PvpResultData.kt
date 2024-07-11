package cn.wthee.pcrtool.data.model

import kotlinx.serialization.Serializable

/**
 * 竞技场查询结果
 */
@Serializable
data class PvpResultData(
    val id: String = "",
    val atk: String = "",
    val def: String = "",
    val region: Int = 2,
    val up: Int = 0,
    val down: Int = 0,
    var atkTalents: String = "",
    var defTalents: String = "",
    var atkPositions: String = "",
    var defPositions: String = "",
)