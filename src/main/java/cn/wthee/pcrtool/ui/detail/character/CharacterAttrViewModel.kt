package cn.wthee.pcrtool.ui.detail.character

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.CharacterRepository
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.data.view.Attr
import cn.wthee.pcrtool.data.view.CharacterStoryAttr.Companion.getAttr
import cn.wthee.pcrtool.data.view.EquipmentMaxData
import cn.wthee.pcrtool.data.view.add
import cn.wthee.pcrtool.data.view.multiply
import cn.wthee.pcrtool.utils.Constants.UNKNOW_EQUIP_ID
import cn.wthee.pcrtool.utils.ToastUtil
import kotlinx.coroutines.launch


class CharacterAttrViewModel(
    private val characterRepository: CharacterRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipments = MutableLiveData<List<EquipmentMaxData>>()
    var storyAttrs = MutableLiveData<Attr>()
    var sumInfo = MutableLiveData<Attr>()
    var maxData = MutableLiveData<List<Int>>()

    //获取角色属性信息
    fun getCharacterInfo(unitId: Int, rank: Int, rarity: Int, lv: Int) {
        //计算属性
        viewModelScope.launch {
            try {
                val rankData = characterRepository.getRankStutas(unitId, rank)
                val rarityData = characterRepository.getRarity(unitId, rarity)
                val ids = characterRepository.getEquipmentIds(unitId, rank).getAllIds()
                //计算指定rank星级下的角色属性
                val info = rankData.attr
                    .add(rarityData.attr)
                    .add(Attr.setGrowthValue(rarityData).multiply(lv + rank))
                val eqs = arrayListOf<EquipmentMaxData>()
                ids.forEach {
                    if (it == UNKNOW_EQUIP_ID)
                        eqs.add(EquipmentMaxData.unknow())
                    else
                        eqs.add(equipmentRepository.getEquipmentData(it))
                }
                //rank装备信息
                equipments.postValue(eqs)
                //计算穿戴装备后属性
                eqs.forEach { eq ->
                    if (eq.equipmentId == UNKNOW_EQUIP_ID) return@forEach
                    info.add(eq.attr)
                }
                //专武
                val uniqueEquip = equipmentRepository.getUniqueEquipInfos(unitId)
                if (uniqueEquip != null) {
                    info.add(uniqueEquip.attr)
                }
                //故事剧情
                val storyAttr = Attr()
                val storyInfo = characterRepository.getCharacterStoryStatus(unitId)
                storyInfo.forEach {
                    storyAttr.add(it.getAttr())
                }
                storyAttrs.postValue(storyAttr)
                info.add(storyAttr)
                sumInfo.postValue(info)
            } catch (e: Exception) {
                ToastUtil.short("角色详细信息暂无~")
            }

        }

    }

    //获取最大Rank和星级
    fun getMaxRankAndRarity(id: Int) {
        viewModelScope.launch {
            try {
                val rank = characterRepository.getMaxRank(id)
                val rarity = characterRepository.getMaxRarity(id)
                val level = characterRepository.getMaxLevel()

                maxData.postValue(listOf(rank, rarity, level))
            } catch (e: Exception) {

            }
        }
    }

    suspend fun isUnknow(id: Int): Boolean {
        try {
            characterRepository.getMaxRank(id)
            characterRepository.getMaxRarity(id)
        } catch (e: Exception) {
            return true
        }
        return false
    }
}
