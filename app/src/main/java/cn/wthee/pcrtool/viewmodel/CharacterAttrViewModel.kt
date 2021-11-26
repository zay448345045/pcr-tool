package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.SkillRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.SkillActionPro
import cn.wthee.pcrtool.data.db.view.getAttr
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.getRankCompareList
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageResourceHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.utils.UMengLogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 角色面板属性 ViewModel
 *
 * @param unitRepository
 * @param equipmentRepository
 *
 */
@HiltViewModel
class CharacterAttrViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    private val skillRepository: SkillRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    val currentValue = MutableLiveData<CharacterProperty>()

    /**
     * 根据角色 id  星级 等级 专武等级
     * 获取角色属性信息 [Attr]
     * @param unitId 角色编号
     * @param property 角色属性
     */
    fun getCharacterInfo(unitId: Int, property: CharacterProperty?) =
        flow {
            if (property != null && property.isInit()) {
                emit(
                    getAttrs(
                        unitId,
                        property.level,
                        property.rank,
                        property.rarity,
                        property.uniqueEquipmentLevel
                    )
                )
            }
        }

    /**
     * 获取角色属性信息
     */
    private suspend fun getAttrs(
        unitId: Int,
        level: Int,
        rank: Int,
        rarity: Int,
        uniqueEquipLevel: Int
    ): AllAttrData {
        val info = Attr()
        val allData = AllAttrData()
        try {
            val rankData = unitRepository.getRankStatus(unitId, rank)
            val rarityData = unitRepository.getRarity(unitId, rarity)
            val bonus = unitRepository.getRankBonus(rank, unitId)
            val ids = unitRepository.getEquipmentIds(unitId, rank).getAllOrderIds()
            //星级属性
            info.add(rarityData.attr)
            //成长属性
            info.add(Attr.setGrowthValue(rarityData).multiply((level + rank).toDouble()))
            //rank属性
            rankData?.let {
                info.add(rankData.attr)
            }
            //奖励属性
            bonus?.let {
                info.add(it.attr)
                allData.bonus = it
            }
            //装备
            val eqs = arrayListOf<EquipmentMaxData>()
            ids.forEach {
                if (it == UNKNOWN_EQUIP_ID || it == 0)
                    eqs.add(EquipmentMaxData.unknown())
                else
                    eqs.add(equipmentRepository.getEquipmentData(it))
            }
            allData.equips = eqs
            //装备属性
            eqs.forEach { eq ->
                if (eq.equipmentId != UNKNOWN_EQUIP_ID) {
                    info.add(eq.attr)
                }
            }
            //专武
            val uniqueEquip = equipmentRepository.getUniqueEquipInfo(unitId, uniqueEquipLevel)
            if (uniqueEquip != null) {
                if (uniqueEquipLevel == 0) {
                    uniqueEquip.attr = Attr()
                }
                info.add(uniqueEquip.attr)
                allData.uniqueEquip = uniqueEquip
            }
            //故事剧情
            val storyAttr = getStoryAttrs(unitId)
            info.add(storyAttr)
            allData.storyAttr = storyAttr
            //被动技能数值
            val skillActionData = getExSkillAttr(unitId, rarity, level)
            val skillAttr = Attr()
            val skillValue = skillActionData.action_value_2 + skillActionData.action_value_3 * level
            when (skillActionData.action_detail_1) {
                1 -> skillAttr.hp = skillValue
                2 -> skillAttr.atk = skillValue
                3 -> skillAttr.def = skillValue
                4 -> skillAttr.magicStr = skillValue
                5 -> skillAttr.magicDef = skillValue
            }
            info.add(skillAttr)
            allData.exSkillAttr = skillAttr
            allData.sumAttr = info
        } catch (e: Exception) {
            if (e !is CancellationException) {
                UMengLogUtil.upload(
                    e, Constants.EXCEPTION_LOAD_ATTR +
                            "uid:$unitId," +
                            "rank:${rank}," +
                            "rarity:${rarity}" +
                            "lv:${level}" +
                            "ueLv:${uniqueEquipLevel}"
                )
            }
        }
        return allData
    }

    /**
     * 获取角色剧情属性
     *
     * @param unitId 角色编号
     */
    private suspend fun getStoryAttrs(unitId: Int): Attr {
        val storyAttr = Attr()
        try {
            val storyInfo = unitRepository.getCharacterStoryStatus(unitId)
            storyInfo.forEach {
                storyAttr.add(it.getAttr())
            }
        } catch (e: Exception) {

        }
        return storyAttr
    }

    private suspend fun getExSkillAttr(unitId: Int, rarity: Int, level: Int): SkillActionPro {
        //100101
        val skillActionId = if (rarity >= 5) {
            unitId / 100 * 1000 + 511
        } else {
            unitId / 100 * 1000 + 501
        } * 100 + 1
        return skillRepository.getSkillActions(level, 0, arrayListOf(skillActionId))[0]
    }

    /**
     * 获取最大Rank和星级等
     *
     * @param unitId 角色编号
     */
    fun getMaxRankAndRarity(unitId: Int) = flow {
        try {
            val rank = unitRepository.getMaxRank(unitId)
            val rarity = unitRepository.getMaxRarity(unitId)
            val level = unitRepository.getMaxLevel()
            val uniqueEquipLevel = equipmentRepository.getUniqueEquipMaxLv()
            emit(CharacterProperty(level, rank, rarity, uniqueEquipLevel))
        } catch (e: Exception) {
            emit(CharacterProperty(level = -1))
        }
    }

    /**
     * 获取指定角色属性对比
     *
     * @param rank0 当前rank
     * @param rank1 目标rank
     */
    fun getUnitAttrCompare(
        unitId: Int,
        level: Int,
        rarity: Int,
        uniqueEquipLevel: Int,
        rank0: Int,
        rank1: Int
    ) = flow {
        val attr0 = getAttrs(unitId, level, rank0, rarity, uniqueEquipLevel)
        val attr1 = getAttrs(unitId, level, rank1, rarity, uniqueEquipLevel)
        emit(getRankCompareList(attr0.sumAttr, attr1.sumAttr))
    }

    /**
     * 获取战力系数
     */
    fun getCoefficient() = flow {
        emit(unitRepository.getCoefficient())
    }

    /**
     * 获取特殊六星 id
     *
     * @param unitId 角色编号
     */
    fun getCutinId(unitId: Int) = flow {
        emit(unitRepository.getCutinId(unitId) ?: 0)
    }
}
