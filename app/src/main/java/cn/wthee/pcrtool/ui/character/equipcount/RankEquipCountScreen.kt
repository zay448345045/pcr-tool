package cn.wthee.pcrtool.ui.character.equipcount

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RankSelectType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.components.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import kotlinx.coroutines.launch

/**
 * rank 范围装备素材数量统计
 */
@SuppressLint("MutableCollectionMutableState")
@Composable
fun RankEquipCountScreen(
    toEquipMaterial: (Int, String) -> Unit,
    rankEquipCountViewModel: RankEquipCountViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by rankEquipCountViewModel.uiState.collectAsStateWithLifecycle()

    //初始收藏信息
    LifecycleEffect(Lifecycle.Event.ON_RESUME) {
        rankEquipCountViewModel.reloadStarList()
    }

    val scrollState = rememberLazyGridState()

    MainScaffold(
        fab = {
            //回到顶部
            var allCount = 0
            uiState.equipmentMaterialList?.forEach {
                allCount += it.count
            }
            MainSmallFab(
                iconType = MainIconType.EQUIP_CALC,
                text = "${uiState.equipmentMaterialList?.size ?: 0} · $allCount",
            ) {
                scope.launch {
                    scrollState.scrollToItem(0)
                }
            }
        },
        secondLineFab = {
            //RANK 选择
            RankRangePickerCompose(
                rank0 = uiState.rank0,
                rank1 = uiState.rank1,
                maxRank = uiState.maxRank,
                openDialog = uiState.openDialog,
                updateRank = rankEquipCountViewModel::updateRank,
                changeDialog = rankEquipCountViewModel::changeDialog,
                type = RankSelectType.LIMIT
            )
        },
        onMainFabClick = {
            scope.launch {
                if (uiState.openDialog) {
                    rankEquipCountViewModel.changeDialog(false)
                } else {
                    navigateUp()
                }
            }
        },
        mainFabIcon = if (uiState.openDialog) MainIconType.CLOSE else MainIconType.BACK,
        enableClickClose = uiState.openDialog,
        onCloseClick = {
            rankEquipCountViewModel.changeDialog(false)
        }
    ) {
        RankEquipCountContent(
            rank0 = uiState.rank0,
            rank1 = uiState.rank1,
            isAllUnit = uiState.isAllUnit,
            loadingState = uiState.loadingState,
            equipmentMaterialList = uiState.equipmentMaterialList,
            scrollState = scrollState,
            starIdList = uiState.starIdList,
            toEquipMaterial = toEquipMaterial
        )
    }

}

@Composable
private fun RankEquipCountContent(
    rank0: Int,
    rank1: Int,
    isAllUnit: Boolean,
    loadingState: LoadingState,
    equipmentMaterialList: List<EquipmentMaterial>?,
    scrollState: LazyGridState,
    starIdList: List<Int>,
    toEquipMaterial: (Int, String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        //标题
        Row(
            modifier = Modifier.padding(
                horizontal = Dimen.largePadding,
                vertical = Dimen.mediumPadding
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainTitleText(text = stringResource(id = if (isAllUnit) R.string.all_unit_calc_equip else R.string.calc_equip_count))
            RankText(
                rank = rank0,
                modifier = Modifier.padding(start = Dimen.mediumPadding)
            )
            MainContentText(
                text = stringResource(id = R.string.to),
                modifier = Modifier.padding(horizontal = Dimen.smallPadding)
            )
            RankText(
                rank = rank1
            )
        }


        LazyVerticalGrid(
            columns = GridCells.Adaptive(Dimen.iconSize + Dimen.mediumPadding),
            contentPadding = PaddingValues(horizontal = Dimen.mediumPadding),
            state = scrollState
        ) {
            when (loadingState) {
                LoadingState.Success -> {
                    items(
                        items = equipmentMaterialList!!,
                        key = {
                            it.id
                        }
                    ) { item ->
                        EquipCountItem(
                            item = item,
                            loved = starIdList.contains(item.id),
                            toEquipMaterial = toEquipMaterial
                        )
                    }
                }

                LoadingState.Loading -> {
                    items(count = 10) {
                        EquipCountItem(
                            item = EquipmentMaterial(),
                            loved = false,
                            toEquipMaterial = toEquipMaterial
                        )
                    }
                }

                LoadingState.Error -> {
                    items(count = 10) {
                        EquipCountItem(
                            item = EquipmentMaterial(-1),
                            loved = false,
                            toEquipMaterial = toEquipMaterial
                        )
                    }
                }

                LoadingState.NoData -> {}
            }
            items(10) {
                CommonSpacer()
            }
        }
    }
}

@Composable
private fun EquipCountItem(
    item: EquipmentMaterial,
    loved: Boolean,
    toEquipMaterial: (Int, String) -> Unit,
) {
    val placeholder = item.id == ImageRequestHelper.UNKNOWN_EQUIP_ID

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(Dimen.mediumPadding)
    ) {
        MainIcon(
            data = ImageRequestHelper.getInstance()
                .getUrl(ImageRequestHelper.ICON_EQUIPMENT, item.id),
            modifier = Modifier.placeholder(placeholder)
        ) {
            toEquipMaterial(item.id, item.name)
        }
        SelectText(
            selected = loved,
            text = item.count.toString(),
        )
    }
}


@CombinedPreviews
@Composable
private fun RankEquipCountContentPreview() {
    PreviewLayout {
        RankEquipCountContent(
            rank0 = 1,
            rank1 = 22,
            isAllUnit = false,
            loadingState = LoadingState.Success,
            equipmentMaterialList = arrayListOf(EquipmentMaterial(1), EquipmentMaterial(2)),
            scrollState = rememberLazyGridState(),
            starIdList = arrayListOf(1),
            toEquipMaterial = { _, _ -> }
        )
    }
}

@CombinedPreviews
@Composable
private fun EquipCountItemPreview() {
    PreviewLayout {
        EquipCountItem(EquipmentMaterial(), false) { _, _ -> }
        EquipCountItem(EquipmentMaterial(), true) { _, _ -> }
    }
}