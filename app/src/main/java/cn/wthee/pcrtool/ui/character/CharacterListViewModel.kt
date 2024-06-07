package cn.wthee.pcrtool.ui.character

import androidx.compose.runtime.Immutable
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.enums.CharacterListShowType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.getData
import cn.wthee.pcrtool.navigation.setData
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.ui.updateLoadState
import cn.wthee.pcrtool.utils.JsonUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：角色列表
 */
@Immutable
data class CharacterListUiState(
    val characterList: List<CharacterInfo>? = null,
    val filter: FilterCharacter? = null,
    //收藏的角色编号
    val favoriteIdList: List<Int> = arrayListOf(),
    val loadState: LoadState = LoadState.Loading,
    val openDialog: Boolean = false,
    val showType: CharacterListShowType = CharacterListShowType.CARD
)

/**
 * 角色列表 ViewModel
 *
 * @param unitRepository
 */
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterListUiState())
    val uiState: StateFlow<CharacterListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val showType = MyApplication.context.dataStoreMain.data
                .first()[MainPreferencesKeys.SP_CHARACTER_LIST_SHOW_TYPE]
                ?: CharacterListShowType.CARD.type
            _uiState.update {
                it.copy(
                    showType = CharacterListShowType.getByValue(showType),
                )
            }
        }
    }


    /**
     * 获取角色基本信息列表
     *
     * @param filter 角色筛选
     */
    private fun getCharacterInfoList(filter: FilterCharacter) {
        viewModelScope.launch {
            val list = unitRepository.getCharacterInfoList(filter, Int.MAX_VALUE)
            _uiState.update {
                it.copy(
                    characterList = list,
                    loadState = updateLoadState(list)
                )
            }
        }
    }

    /**
     * 获取筛选信息
     */
    fun initFilter() {
        viewModelScope.launch {
            val filterData = getData<String>(NavRoute.FILTER_DATA)
            val filter: FilterCharacter? = JsonUtil.fromJson(filterData)
            val idList = unitRepository.getUnitIdList()
            val favoriteIdList = FilterCharacter.getFavoriteIdList().filter { id ->
                //筛选出当前区服版本下的收藏角色
                idList.find {
                    it == id
                } != null
            }

            val initFilter = filter ?: FilterCharacter()
            _uiState.update {
                it.copy(
                    filter = initFilter,
                    favoriteIdList = favoriteIdList
                )
            }

            //初始加载
            getCharacterInfoList(initFilter)
        }
    }

    /**
     * 重置筛选
     */
    fun resetFilter() {
        viewModelScope.launch {
            val filter = FilterCharacter()
            val list = unitRepository.getCharacterInfoList(filter, Int.MAX_VALUE)
            setData(NavRoute.FILTER_DATA, null)
            _uiState.update {
                it.copy(
                    filter = filter,
                    characterList = list,
                    loadState = updateLoadState(list)
                )
            }
        }
    }

    /**
     * 弹窗状态更新
     */
    fun changeDialog(openDialog: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openDialog = openDialog
                )
            }
        }
    }

    /**
     * 展示方式更新
     */
    fun changeShowType() {
        viewModelScope.launch {
            val showType = when (_uiState.value.showType) {
                CharacterListShowType.CARD -> CharacterListShowType.ICON_TAG
                CharacterListShowType.ICON_TAG -> CharacterListShowType.ICON
                CharacterListShowType.ICON -> CharacterListShowType.CARD
            }
            MyApplication.context.dataStoreMain.edit { preferences ->
                preferences[MainPreferencesKeys.SP_CHARACTER_LIST_SHOW_TYPE] = showType.type
            }
            _uiState.update {
                it.copy(
                    showType = showType
                )
            }
        }
    }
}

