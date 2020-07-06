package cn.wthee.pcrtool.ui.detail.character

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.CharacterRepository
import cn.wthee.pcrtool.data.EquipmentRepository
import cn.wthee.pcrtool.data.model.CharacterAttrInfo
import cn.wthee.pcrtool.data.model.EquipmentData
import cn.wthee.pcrtool.data.model.add
import cn.wthee.pcrtool.data.model.multiply
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CharacterPromotionViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    var equipments = MutableLiveData<List<EquipmentData>>()
    var sumInfo = MutableLiveData<CharacterAttrInfo>()
    var maxRankAndRarity = MutableLiveData<List<Int>>()

    //获取角色属性信息
    fun getCharacterInfo(unitId: Int, rank: Int, rarity: Int, lv: Int) {
        //计算属性
        viewModelScope.launch {
            val rankData = characterRepository.getRankStutas(unitId, rank)
            val rarityData = characterRepository.getRarity(unitId, rarity)
            val ids = characterRepository.getEquipmentIds(unitId, rank).getAllIds()
            //计算指定rank星级下的角色属性
            val info = CharacterAttrInfo.setValue(rankData)
                .add(CharacterAttrInfo.setValue(rarityData))
                .add(CharacterAttrInfo.setGrowthValue(rarityData).multiply(lv + rank))
            //重复装备数
            CharacterViewModel.repeat.clear()
            ids.forEach {
                CharacterViewModel.repeat[it] = Collections.frequency(ids, it)
            }
            val eqs = equipmentRepository.getEquipmentDatas(ids)
            //rank装备信息
            equipments.postValue(eqs)
            //计算穿戴装备后属性
            eqs.forEach { eq ->
                val mult = when (eq.promotionLevel) {
                    1 -> 0
                    2 -> 1
                    3 -> 3
                    4, 5 -> 5
                    else -> 0
                }
                val count = CharacterViewModel.repeat[eq.equipmentId] ?: 0
                //获取装备信息及其提升
                val eqInfo = CharacterAttrInfo.setValue(eq)
                val eh =
                    CharacterAttrInfo.setValue(equipmentRepository.getEquipmentEnhanceData(eq.equipmentId))
                for (i in 0..count) {
                    info.add(eh.multiply(mult))
                        .add(eqInfo)
                }
            }
            sumInfo.postValue(info)
        }

    }

    //获取最大Rank和星级
    fun getMaxRankAndRarity(id: Int) {
        viewModelScope.launch {
            val rank = characterRepository.getMaxRank(id)
            val rarity = characterRepository.getMaxRarity(id)
            maxRankAndRarity.postValue(listOf(rank, rarity))
        }
    }

}
