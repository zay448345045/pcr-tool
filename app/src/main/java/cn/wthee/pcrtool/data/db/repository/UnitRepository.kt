package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.SkillDao
import cn.wthee.pcrtool.data.db.dao.UnitDao
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.CharacterProfileInfo
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.RoomCommentData
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import cn.wthee.pcrtool.data.db.view.getAttr
import cn.wthee.pcrtool.data.enums.CharacterSortType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.second
import javax.inject.Inject

/**
 * 角色 Repository
 *
 * @param unitDao
 */
class UnitRepository @Inject constructor(
    private val unitDao: UnitDao,
    private val skillDao: SkillDao,
    private val equipmentRepository: EquipmentRepository
) {

    suspend fun getUnitIdList() = try {
        unitDao.getUnitIdList()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getUnitIdList")
        arrayListOf()
    }

    /**
     * 获取角色列表
     */
    suspend fun getCharacterInfoList(filter: FilterCharacter, limit: Int): List<CharacterInfo>? {
        try {
            //额外角色编号
            val exUnitIdList = try {
                unitDao.getExUnitIdList()
            } catch (_: Exception) {
                emptyList()
            }

            var filterList = unitDao.getCharacterInfoList(
                sortType = filter.sortType.type,
                asc = if (filter.asc) "asc" else "desc",
                unitName = filter.name,
                pos1 = filter.position()[0],
                pos2 = filter.position()[1],
                atkType = filter.atk,
                guildId = when {
                    //公会
                    filter.guild > 1 -> getGuilds()[filter.guild - 2].guildId
                    //无公会
                    filter.guild == 1 -> -1
                    //全部
                    else -> 0
                },
                //六星排序时，仅显示六星角色
                r6 = if (filter.sortType == CharacterSortType.SORT_UNLOCK_6) 1 else filter.r6,
                type = filter.type,
                limit = limit,
                exUnitIdList = exUnitIdList,
                raceName = when {
                    //种族
                    filter.race > 1 -> getRaces()[filter.race - 2]
                    //多人卡
                    filter.race == 1 -> "-"
                    //全部
                    else -> ""
                },
            )

            //筛选专用装备
            val uniqueEquipList = equipmentRepository.getUniqueEquipList("", 0)
            filterList.forEach {
                val uniqueEquipType = uniqueEquipList?.count { equip ->
                    equip.unitId == it.id
                } ?: 0
                it.uniqueEquipType = uniqueEquipType
            }
            filterList = when (filter.uniqueEquipType) {
                1, 2 -> filterList.filter {
                    it.uniqueEquipType == filter.uniqueEquipType
                }

                3 -> filterList.filter {
                    it.uniqueEquipType == 0
                }

                else -> filterList
            }

            //筛选天赋类型
            val talentIdList = getTalentIdList(0)
            filterList.forEach {
                it.talentId = if (talentIdList.isNotEmpty()) {
                    talentIdList.find { talent -> talent.unitId == it.id }?.talentId ?: 0
                } else {
                    0
                }
            }
            filterList = when (filter.talentType) {
                1, 2, 3, 4, 5 -> filterList.filter {
                    it.talentId == filter.talentType
                }

                else -> filterList
            }

            //按日期排序时，由于数据库部分日期格式有问题，导致排序不对，需要重新排序
            if (filter.sortType == CharacterSortType.SORT_DATE) {
                filterList = filterList.sortedWith { o1, o2 ->
                    val second = o1.startTime.second(o2.startTime)
                    when {
                        second > 0 -> 1
                        second == 0L -> {
                            o2.gachaId.compareTo(o1.gachaId)
                        }
                        else -> -1
                    } * (if (filter.asc) 1 else -1)
                }
            }


            return if (filter.all) {
                filterList
            } else {
                //筛选收藏的角色
                val favoriteIdList = FilterCharacter.getFavoriteIdList()
                filterList.filter {
                    favoriteIdList.contains(it.id)
                }
            }
        } catch (e: Exception) {
            LogReportUtil.upload(
                e,
                Constants.EXCEPTION_UNIT_NULL + "getCharacterInfoList#params:${filter}"
            )
            return null
        }
    }

    /**
     * 获取角色数量，总数 (未实装数)
     */
    suspend fun getCount() = try {
        val unknownCount = unitDao.getUnknownCount()
        unitDao.getCount().toString() + if (unknownCount > 0) " (${unknownCount})" else ""
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getCharacterCount")
        "0"
    }

    /**
     * 获取角色数量
     */
    suspend fun getCountInt() = try {
        unitDao.getCount()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getCountInt")
        0
    }

    /**
     * 获取角色基本信息
     */
    suspend fun getCharacterInfo(unitId: Int) = try {
        //额外角色编号
        val exUnitIdList = try {
            unitDao.getExUnitIdList()
        } catch (_: Exception) {
            arrayListOf()
        }

        val data = unitDao.getCharacterInfo(unitId = unitId, exUnitIdList = exUnitIdList)!!
        //获取专用装备信息
        val uniqueEquipList = equipmentRepository.getUniqueEquipList("", 0, unitId = data.id)
        data.uniqueEquipType = uniqueEquipList?.size ?: 0
        //获取天赋类型
        val talentIdList = getTalentIdList(unitId)
        data.talentId = if (talentIdList.isNotEmpty()) {
            talentIdList[0].talentId
        } else {
            0
        }

        //返回数据
        data
    } catch (_: Exception) {
        null
    }

    /**
     * 获取角色资料
     */
    suspend fun getProfileInfo(unitId: Int): CharacterProfileInfo? {
        //校验是否未多角色卡
        val data = unitDao.getProfileInfo(unitId)
        if (data == null) {
            LogReportUtil.upload(
                NullPointerException(),
                Constants.EXCEPTION_UNIT_NULL + "unit_id:$unitId"
            )
        }
        return data
    }

    /**
     * 根据站位获取角色列表
     */
    suspend fun getCharacterByPosition(start: Int, end: Int) = try {
        unitDao.getCharacterByPosition(start, end)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getCharacterByPosition$start-$end")
        emptyList()
    }

    /**
     * 根据id列表获取角色列表
     */
    suspend fun getCharacterByIds(unitIds: List<Int>) = try {
        unitDao.getCharacterByIds(unitIds)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getCharacterByIds$unitIds")
        emptyList()
    }

    suspend fun getMaxRank(unitId: Int) = try {
        unitDao.getMaxRank(unitId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getMaxRank$unitId")
        0
    }

    suspend fun getMaxRarity(unitId: Int) = try {
        unitDao.getMaxRarity(unitId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getMaxRarity$unitId")
        0
    }

    suspend fun getGuilds() = try {
        unitDao.getGuilds()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getGuilds")
        emptyList()
    }

    suspend fun getRaces() = try {
        unitDao.getRaces()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getRaces")
        emptyList()
    }

    suspend fun getAllGuildMembers() = try {
        unitDao.getAllGuildMembers()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getAllGuildMembers")
        emptyList()
    }

    suspend fun getNoGuildMembers() = try {
        unitDao.getNoGuildMembers()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getNoGuildMembers")
        null
    }

    suspend fun getR6Ids() = try {
        unitDao.getR6Ids()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getR6Ids")
        null
    }

    suspend fun getCharacterStoryAttrList(unitId: Int) = try {
        unitDao.getCharacterStoryAttrList(unitId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getCharacterStoryAttrList:$unitId")
        emptyList()
    }

    suspend fun getMaxLevel() = try {
        unitDao.getMaxLevel()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getMaxLevel")
        0
    }

    suspend fun getCoefficient() = try {
        unitDao.getCoefficient()
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getCoefficient")
        null
    }

    suspend fun getCutinId(unitId: Int) = try {
        unitDao.getCutinId(unitId) ?: 0
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getCutinId;$unitId")
        0
    }

    suspend fun getSummonData(unitId: Int) = try {
        unitDao.getSummonData(unitId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getSummonData")
        null
    }

    suspend fun getActualId(unitId: Int) = try {
        unitDao.getActualId(unitId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getActualId")
        null
    }

    /**
     * 获取卡池角色，不包括额外角色
     */
    suspend fun getGachaUnits(type: Int) = try {
        //额外角色编号
        val exUnitIdList = try {
            unitDao.getExUnitIdList()
        } catch (_: Exception) {
            arrayListOf()
        }
        unitDao.getGachaUnits(type, exUnitIdList)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getGachaUnits")
        emptyList()
    }

    suspend fun getHomePageComments(unitId: Int) = try {
        unitDao.getHomePageComments(unitId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getHomePageComments:$unitId")
        emptyList()
    }

    suspend fun getAtkCastTime(unitId: Int) = try {
        unitDao.getAtkCastTime(unitId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getAtkCastTime:$unitId")
        null
    }

    /**
     * 获取角色属性信息
     */
    suspend fun getAttrs(
        unitId: Int,
        level: Int,
        rank: Int,
        rarity: Int,
        uniqueEquipLevel: Int,
        uniqueEquipLevel2: Int
    ): AllAttrData {
        val info = Attr()
        val allData = AllAttrData()
        try {
            //RANK 奖励属性
            try {
                val bonus = unitDao.getRankBonus(rank = rank, unitId = unitId)
                bonus?.let {
                    info.add(it.attr)
                    allData.rankBonus = it
                }
            } catch (_: Exception) {

            }

            //星级属性
            val rarityData = unitDao.getRarity(unitId = unitId, rarity = rarity)
            info.add(rarityData.attr)

            //成长属性
            info.add(Attr.setGrowthValue(rarityData).multiply((level + rank).toDouble()))

            //RANK 属性
            val rankData = unitDao.getRankStatus(unitId = unitId, rank = rank)
            rankData?.let {
                info.add(rankData.attr)
            }

            //装备
            try {
                val equipIds =
                    unitDao.getRankEquipment(unitId = unitId, rank = rank).getAllOrderIds()
                val eqs = arrayListOf(
                    EquipmentMaxData(),
                    EquipmentMaxData(),
                    EquipmentMaxData(),
                    EquipmentMaxData(),
                    EquipmentMaxData(),
                    EquipmentMaxData(),
                )
                equipIds.forEachIndexed { index, id ->
                    if (!(id == ImageRequestHelper.UNKNOWN_EQUIP_ID || id == 0)) {
                        equipmentRepository.getEquipmentData(id)?.let { equip ->
                            eqs[index] = equip
                        }
                    }
                }
                allData.equips = eqs

                //装备属性
                eqs.forEach { eq ->
                    if (eq.equipmentId != ImageRequestHelper.UNKNOWN_EQUIP_ID) {
                        info.add(eq.attr)
                    }
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, Constants.EXCEPTION_LOAD_ATTR + "equip_error:$unitId")
            }

            //专武
            try {
                val uniqueEquip = equipmentRepository.getUniqueEquipInfo(
                    unitId = unitId,
                    lv = uniqueEquipLevel,
                    lv2 = uniqueEquipLevel2
                )
                if (uniqueEquip.isNotEmpty()) {
                    val uniqueEquipList = arrayListOf<UniqueEquipmentMaxData>()
                    uniqueEquip.forEach {
                        if (uniqueEquipLevel == 0) {
                            it.attr = Attr()
                        }
                        info.add(it.attr)
                        uniqueEquipList.add(it)
                    }
                    allData.uniqueEquipList = uniqueEquipList
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, Constants.EXCEPTION_LOAD_ATTR + "uq_error:$unitId")
            }

            //故事剧情
            val storyAttr = getStoryAttrs(unitId)
            info.add(storyAttr)
            allData.storyAttr = storyAttr

            //被动技能数值
            val skillActionData = getExSkillAttr(unitId = unitId, rarity = rarity, level = level)
            val skillAttr = Attr()
            val skillValue = skillActionData.actionValue2 + skillActionData.actionValue3 * level
            when (skillActionData.actionDetail1) {
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
            LogReportUtil.upload(
                e, Constants.EXCEPTION_LOAD_ATTR +
                        "getAttrs#uid:$unitId," +
                        "rank:${rank}," +
                        "rarity:${rarity}" +
                        "lv:${level}" +
                        "ueLv:${uniqueEquipLevel}"
            )
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
            val storyInfo = getCharacterStoryAttrList(unitId)
            storyInfo.forEach {
                storyAttr.add(it.getAttr())
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getStoryAttrs:$unitId")
        }
        return storyAttr
    }


    /**
     * 获取被动技能数据
     */
    private suspend fun getExSkillAttr(unitId: Int, rarity: Int, level: Int): SkillActionDetail {
        //100101
        val skillActionId = if (rarity >= 5) {
            unitId / 100 * 1000 + 511
        } else {
            unitId / 100 * 1000 + 501
        } * 100 + 1
        val list = try {
            skillDao.getSkillActions(
                lv = level,
                atk = 0,
                actionIds = arrayListOf(skillActionId),
                isRfSkill = false,
                isOtherRfSkill = false
            )
        } catch (e: Exception) {
            emptyList()
        }

        return if (list.isNotEmpty()) {
            list[0]
        } else {
            SkillActionDetail()
        }
    }

    /**
     *  校验是否为多角色卡
     */
    suspend fun getMultiIds(unitId: Int): ArrayList<Int> = try {
        val ids = arrayListOf(unitId)
        val multiIds = unitDao.getMultiIds(unitId)
        if (multiIds.isNotEmpty()) {
            ids.addAll(multiIds)
        }
        ids
    } catch (_: Exception) {
        arrayListOf()
    }

    /**
     * 获取角色小屋对话
     *
     * @param unitId 角色编号
     */
    suspend fun getRoomComments(unitId: Int): ArrayList<RoomCommentData> {
        val ids = getMultiIds(unitId)
        val commentList = arrayListOf<RoomCommentData>()
        ids.forEach {
            val data = unitDao.getRoomComments(it)
            if (data != null) {
                commentList.add(data)
            }
        }

        return commentList
    }

    suspend fun getCharacterInfo(unitId: Int, property: CharacterProperty?) = try {
        if (property != null && property.isInit()) {
            getAttrs(
                unitId = unitId,
                level = property.level,
                rank = property.rank,
                rarity = property.rarity,
                uniqueEquipLevel = property.uniqueEquipmentLevel,
                uniqueEquipLevel2 = property.uniqueEquipmentLevel2
            )
        } else {
            null
        }
    } catch (e: Exception) {
        LogReportUtil.upload(
            e,
            "getCharacterInfo#unitId:$unitId,property:${property ?: ""}"
        )
        null
    }

    suspend fun getTalentIdList(unitId: Int, talentType: Int = 0) = try {
        unitDao.getTalentIdList(unitId, talentType)
    } catch (_: Exception) {
        arrayListOf()
    }

    /**
     * 获取角色id按天赋分组
     */
    suspend fun getTalentUnitMap(): HashMap<Int, ArrayList<Int>> {
        val list = getTalentIdList(0)
        val map = hashMapOf<Int, ArrayList<Int>>()
        list.forEach {
            if (map[it.talentId] == null) {
                map[it.talentId] = arrayListOf()
            } else {
                map[it.talentId]?.add(it.unitId)
            }
        }
        return map
    }
}