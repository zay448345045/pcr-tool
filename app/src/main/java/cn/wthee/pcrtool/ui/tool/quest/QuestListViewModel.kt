package cn.wthee.pcrtool.ui.tool.quest

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.QuestRepository
import cn.wthee.pcrtool.data.db.view.QuestDetail
import cn.wthee.pcrtool.data.model.RandomEquipDropArea
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：主线地图
 */
@Immutable
data class QuestListUiState(
    val questList: List<QuestDetail>? = null,
    //额外掉落
    val randomDropResponseData: ResponseData<List<RandomEquipDropArea>>? = null
)


/**
 * 主线地图 ViewModel
 *
 * @param questRepository
 */
@HiltViewModel
class QuestListViewModel @Inject constructor(
    private val questRepository: QuestRepository,
    private val apiRepository: MyAPIRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuestListUiState())
    val uiState: StateFlow<QuestListUiState> = _uiState.asStateFlow()

    init {
        getQuestList()
        getDropAreaList()
    }


    /**
     * 获取装备掉落关卡信息
     *
     */
    private fun getQuestList() = flow {
        try {
            val info = questRepository.getEquipDropQuestList(0)
            emit(info)
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getQuestList")
        }
        viewModelScope.launch {
            val info = questRepository.getEquipDropQuestList(0)
            _uiState.update {
                it.copy(
                    questList = info
                )
            }
        }
    }

    /**
     * 获取掉落地区信息
     */
    private fun getDropAreaList() {
        viewModelScope.launch {
            try {
                val response = apiRepository.getEquipArea(0)
                response.data?.let {
                    val maxArea = equipmentRepository.getMaxArea() % 100
                    val filterList = it.filter { areaData -> areaData.area <= maxArea }
                    response.data = filterList
                }

                _uiState.update {
                    it.copy(
                        randomDropResponseData = response
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getEquipAreaList")
            }
        }
    }
}
