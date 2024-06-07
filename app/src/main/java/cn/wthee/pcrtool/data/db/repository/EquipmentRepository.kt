package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.EquipmentDao
import cn.wthee.pcrtool.data.db.view.EquipmentBasicInfo
import cn.wthee.pcrtool.data.db.view.UniqueEquipmentMaxData
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.FilterEquip
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import javax.inject.Inject

/**
 * 装备Repository
 *
 * @param equipmentDao
 */
class EquipmentRepository @Inject constructor(private val equipmentDao: EquipmentDao) {

    suspend fun getEquipmentData(equipId: Int) = try {
        equipmentDao.getEquipInfo(equipId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipmentData#equipId:$equipId")
        null
    }

    suspend fun getEquipmentList(filter: FilterEquip, limit: Int) = try {
        val filterList = equipmentDao.getEquipmentList(
            craft = filter.craft,
            colorType = filter.colorType,
            name = filter.name,
            limit = limit
        )

        if (filter.all) {
            filterList
        } else {
            //筛选收藏的
            val favoriteIdList = FilterEquip.getFavoriteIdList()
            filterList.filter {
                favoriteIdList.contains(it.equipmentId)
            }
        }
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipmentList#filter:$filter")
        null
    }

    suspend fun getCount() = try {
        equipmentDao.getCount()
    } catch (_: Exception) {
        0
    }

    /**
     * 获取专用装备1、2数量
     */
    suspend fun getUniqueEquipCount() = try {
        val uniqueEquipCount = equipmentDao.getUniqueEquipCountV2()
        if (uniqueEquipCount.size > 1) {
            "${uniqueEquipCount[0].count} · ${uniqueEquipCount[1].count}"
        } else {
            uniqueEquipCount[0].count.toString()
        }

    } catch (_: Exception) {
        try {
            val uniqueEquipCount = equipmentDao.getUniqueEquipCount()
            if (uniqueEquipCount.isNotEmpty()) {
                uniqueEquipCount[0].count.toString()
            } else {
                "0"
            }
        } catch (_: Exception) {
            "0"
        }
    }

    /**
     * 获取专用装备信息（包括专用装备1、2）
     *
     * @param unitId 角色编号
     * @param lv 专用装备1等级
     * @param lv2 专用装备2等级
     */
    suspend fun getUniqueEquipInfo(unitId: Int, lv: Int, lv2: Int) =
        if (lv > Constants.TP_LIMIT_LEVEL) {
            //tp相关261 ~ 300
            val tpBonusAttr =
                getUniqueEquipBonus(
                    unitId = unitId,
                    offsetLv = lv - Constants.TP_LIMIT_LEVEL,
                    minLv = Constants.TP_LIMIT_LEVEL
                )
            // 回避等相关301 ~
            val otherBonusAttr =
                getUniqueEquipBonus(
                    unitId = unitId,
                    offsetLv = lv - Constants.OTHER_LIMIT_LEVEL,
                    minLv = Constants.OTHER_LIMIT_LEVEL
                )

            val level = if (tpBonusAttr != null) {
                //带tp相关属性，仅计算260级之前的属性
                Constants.TP_LIMIT_LEVEL
            } else if (otherBonusAttr != null) {
                //带回避相关属性，仅计算300级之前的属性
                Constants.OTHER_LIMIT_LEVEL
            } else {
                //正常计算等级提升属性
                lv
            }

            val maxDataList = getUniqueEquip(unitId = unitId, lv = level, lv2 = lv2)
            // 专武1奖励属性不为空，计算总属性：初始属性 + 奖励属性
            if (maxDataList.isNotEmpty() && maxDataList[0].equipmentId % 10 == 1) {
                if (tpBonusAttr != null) {
                    maxDataList[0].isTpLimitAction = true
                    maxDataList[0].attr = maxDataList[0].attr.add(tpBonusAttr)
                }
                if (otherBonusAttr != null) {
                    maxDataList[0].isOtherLimitAction = true
                    maxDataList[0].attr = maxDataList[0].attr.add(otherBonusAttr)
                }
            }

            maxDataList
        } else {
            getUniqueEquip(unitId = unitId, lv = lv, lv2 = lv2)
        }


    /**
     * 查询两张专武关联表，适配不同游戏版本
     */
    private suspend fun getUniqueEquip(
        unitId: Int,
        lv: Int,
        lv2: Int
    ): List<UniqueEquipmentMaxData> {
        val list = arrayListOf<UniqueEquipmentMaxData>()
        try {
            //专用装备1
            equipmentDao.getUniqueEquipInfoV2(unitId = unitId, lv = lv, slot = 1)?.let {
                list.add(it)
            }
            //专用装备2
            equipmentDao.getUniqueEquipInfoV2(unitId = unitId, lv = lv2 + 1, slot = 2)?.let {
                list.add(it)
            }
        } catch (e: Exception) {
            equipmentDao.getUniqueEquipInfo(unitId = unitId, lv = lv)?.let {
                list.add(it)
            }
//            LogReportUtil.upload(e, "getUniqueEquip#unitId:$unitId")
        }
        return list
    }

    /**
     * 查询两张专武关联表，适配不同游戏版本
     */
    private suspend fun getUniqueEquipBonus(unitId: Int, offsetLv: Int, minLv: Int) = try {
        equipmentDao.getUniqueEquipBonusV2(unitId = unitId, lv = offsetLv, minLv = minLv)
    } catch (e: Exception) {
//        LogReportUtil.upload(e, "getUniqueEquipBonus#unitId:$unitId,offsetLv:$offsetLv,minLv:$minLv")
//        Attr()
        equipmentDao.getUniqueEquipBonus(unitId = unitId, lv = offsetLv, minLv = minLv)
    }

    suspend fun getUniqueEquipMaxLv(slot: Int) = equipmentDao.getUniqueEquipMaxLv(slot)

    /**
     * 获取所有角色所需的装备统计
     */
    suspend fun getEquipByRank(unitId: Int, startRank: Int, endRank: Int) = try {
        val data = equipmentDao.getEquipByRank(
            unitId = unitId,
            startRank = startRank,
            endRank = endRank
        )
        //计算倍数
        val materials = arrayListOf<EquipmentMaterial>()
        data.forEach { equipCountData ->
            try {
                val equip = equipmentDao.getEquipBasicInfo(equipCountData.equipId)
                val material = getEquipCraft(equip)
                material.map {
                    it.count *= equipCountData.equipCount
                }
                materials.addAll(material)
            } catch (_: Exception) {
            }

        }
        //合并重复项
        val map = mutableMapOf<Int, EquipmentMaterial>()
        materials.forEach {
            var i = it.count
            val key = it.id
            if (map[key] != null) {
                i = map[key]!!.count + it.count
            }
            it.count = i
            map[key] = it
        }
        //转换为列表
        map.values.sortedByDescending {
            it.count
        }
    } catch (e: Exception) {
        LogReportUtil.upload(
            e,
            "getEquipByRank#unitId:$unitId,startRank:$startRank,endRank:$endRank"
        )
        null
    }

    suspend fun getRankEquipList(unitId: Int) = try {
        equipmentDao.getRankEquipList(unitId)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getAllRankEquipList#unitId:$unitId")
        null
    }

    suspend fun getMaxArea() = equipmentDao.getMaxArea()

    suspend fun getEquipUnitList(equipId: Int) = equipmentDao.getEquipUnitList(equipId)

    suspend fun getEquipColorNum() = try {
        equipmentDao.getEquipColorNum()
    } catch (_: Exception) {
        0
    }

    suspend fun getMaxRank() = try {
        equipmentDao.getMaxRank()
    } catch (_: Exception) {
        0
    }

    /**
     * 获取排序后的专用装备列表
     */
    suspend fun getUniqueEquipList(name: String, slot: Int, unitId: Int = 0) = try {
        val data = (try {
            val data = equipmentDao.getUniqueEquipListV2(name = name, slot = slot, unitId = unitId)
            data
        } catch (_: Exception) {
            equipmentDao.getUniqueEquipList(name = name, slot = slot, unitId = unitId)
        }).reversed()

        when (MainActivity.regionType) {
            RegionType.CN -> {
                //处理国服排序
                data.sortedBy {
                    arrayListOf(
                        137011,
                        137021
                    ).contains(it.equipId)
                }
            }

            RegionType.TW -> {
                //处理台服排序
                data.sortedBy {
                    arrayListOf(
                        138011,
                        138021,
                        138041,
                        138061
                    ).contains(it.equipId)
                }
            }

            else -> {
                data
            }
        }
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getUniqueEquipInfoList")
        null
    }


    /**
     * 获取装备制作材料信息
     *
     * @param equip 装备信息
     */
    suspend fun getEquipCraft(equip: EquipmentBasicInfo) = try {
        val materials = arrayListOf<EquipmentMaterial>()
        if (equip.craftFlg == 0) {
            materials.add(
                EquipmentMaterial(
                    id = equip.equipmentId,
                    name = equip.equipmentName,
                    count = 1
                )
            )
        } else {
            getAllMaterial(
                materials = materials,
                equipmentId = equip.equipmentId,
                name = equip.equipmentName,
                count = 1,
                craftFlg = 1
            )
        }
        materials
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipCraft#equip:${equip.equipmentId}")
        emptyList()
    }

    /**
     * 迭代获取合成材料
     *
     * @param materials 合成素材
     * @param equipmentId 装备编号
     * @param name 装备名称
     * @param count 所需数量
     * @param craftFlg 是否可合成 0：不可，1：可合成
     */
    private suspend fun getAllMaterial(
        materials: ArrayList<EquipmentMaterial>,
        equipmentId: Int,
        name: String,
        count: Int,
        craftFlg: Int
    ) {
        try {
            if (craftFlg == 1) {
                //迭代获取所有素材数量
                val material = equipmentDao.getEquipmentCraft(equipmentId).getAllMaterialId()
                material.forEach {
                    val data = equipmentDao.getEquipBasicInfo(it.id)
                    getAllMaterial(
                        materials = materials,
                        equipmentId = data.equipmentId,
                        name = data.equipmentName,
                        count = it.count,
                        craftFlg = data.craftFlg
                    )
                }
            } else {
                //直接获取素材数量
                val material =
                    EquipmentMaterial(
                        id = equipmentId,
                        name = name,
                        count = count
                    )
                var flag = -1
                materials.forEachIndexed { index, equipmentMaterial ->
                    if (equipmentMaterial.id == material.id) {
                        flag = index
                    }
                }
                //素材数量统计
                if (flag == -1) {
                    //不存在，添加
                    materials.add(material)
                } else {
                    //已存在，累加数量
                    materials[flag].count += material.count
                }
            }
        } catch (_: Exception) {

        }
    }
}