package cn.wthee.pcrtool.ui.tool.mockgacha

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.db.view.MockGachaProData
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.GridIconList
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.LifecycleEffect
import cn.wthee.pcrtool.ui.components.MainAlertDialog
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.utils.toDate
import kotlinx.coroutines.launch

/**
 * 历史卡池
 */
@Composable
fun MockGachaHistory(
    scrollState: LazyGridState,
    mockGachaViewModel: MockGachaViewModel = hiltViewModel(),
) {
    val uiState by mockGachaViewModel.uiState.collectAsStateWithLifecycle()
    LifecycleEffect(Lifecycle.Event.ON_RESUME) {
        mockGachaViewModel.getHistory()
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        columns = GridCells.Adaptive(getItemWidth())
    ) {
        items(
            items = uiState.historyList,
            key = {
                it.gachaId
            }
        ) {
            MockGachaHistoryItem(
                gachaData = it,
                changeGachaId = mockGachaViewModel::changeGachaId,
                changeShowResult = mockGachaViewModel::changeShowResult,
                changeSelect = mockGachaViewModel::changeSelect,
                updatePickUpList = mockGachaViewModel::updatePickUpList,
                deleteGachaByGachaId = mockGachaViewModel::deleteGachaByGachaId,
            )
        }
        item {
            CommonSpacer()
        }
        item {
            CommonSpacer()
        }
    }
}

/**
 * 卡池历史记录 item
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MockGachaHistoryItem(
    gachaData: MockGachaProData,
    changeGachaId: (String) -> Unit,
    changeShowResult: (Boolean) -> Unit,
    changeSelect: (Int) -> Unit,
    updatePickUpList: (List<GachaUnitInfo>) -> Unit,
    deleteGachaByGachaId: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val openDialog = remember {
        mutableStateOf(false)
    }

    var upCount = 0
    var start3Count = 0
    val pickUpUnitIds = gachaData.pickUpIds.intArrayList
    val resultIds = gachaData.resultUnitIds.intArrayList
    val resultCount = resultIds.size / 10

    resultIds.forEachIndexed { index, unitId ->
        if (pickUpUnitIds.contains(unitId)) {
            upCount++
        }
        if (gachaData.resultUnitRaritys.intArrayList[index] == 3) {
            start3Count++
        }
    }

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        //标题
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.smallPadding)
        ) {
            MainTitleText(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = gachaData.createTime.formatTime.toDate
            )
            //up个数
            if (upCount > 0) {
                MainTitleText(
                    modifier = Modifier
                        .padding(start = Dimen.smallPadding)
                        .align(Alignment.CenterVertically),
                    text = "UP：$upCount",
                    backgroundColor = colorRed
                )
            }
            //3星个数
            if (start3Count > 0) {
                MainTitleText(
                    modifier = Modifier
                        .padding(start = Dimen.smallPadding)
                        .align(Alignment.CenterVertically),
                    text = stringResource(id = R.string.star, 3) + "：$start3Count",
                    backgroundColor = colorGold,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            //去抽卡
            IconTextButton(
                icon = MainIconType.MOCK_GACHA_PAY,
                text = stringResource(id = R.string.go_to_mock),
                onClick = {
                    scope.launch {
                        changeGachaId(gachaData.gachaId)
                        //卡池详情
                        val newPickUpList = arrayListOf<GachaUnitInfo>()
                        gachaData.pickUpIds.intArrayList.forEach {
                            newPickUpList.add(
                                GachaUnitInfo(
                                    unitId = it,
                                    unitName = "",
                                    isLimited = -1,
                                    rarity = 3
                                )
                            )
                        }
                        updatePickUpList(newPickUpList)
                        changeSelect(gachaData.gachaType)
                        //显示卡池结果
                        changeShowResult(true)
                    }
                }
            )
        }

        MainCard {
            //up 角色
            val idList = arrayListOf<Int>()
            gachaData.pickUpIds.intArrayList.forEach { unitId ->
                idList.add(unitId + 30)
            }
            GridIconList(
                idList = idList,
                iconResourceType = IconResourceType.CHARACTER,
                onClickItem = { }
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = Dimen.mediumPadding)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //删除操作
                IconTextButton(
                    icon = MainIconType.DELETE,
                    text = stringResource(R.string.delete_gacha),
                    contentColor = colorRed,
                    onClick = {
                        openDialog.value = true
                    }
                )
                //日期
                CaptionText(
                    text = stringResource(
                        id = R.string.last_gacha_date,
                        resultCount,
                        gachaData.lastUpdateTime
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    //删除卡池提示
    MainAlertDialog(
        openDialog = openDialog,
        icon = MainIconType.DELETE,
        title = stringResource(id = R.string.title_dialog_delete),
        text = stringResource(id = R.string.tip_delete_gacha),
    ) {
        deleteGachaByGachaId(gachaData.gachaId)
    }

}


@CombinedPreviews
@Composable
private fun MockGachaHistoryItemPreview() {
    PreviewLayout {
        MockGachaHistoryItem(
            gachaData = MockGachaProData(
                gachaType = 1,
                pickUpIds = "1-2",
                resultUnitIds = "1-2-3-4-5-5-4-3-2-1",
                resultUnitRaritys = "1-2-3-1-2-3-1-2-3-1",
                createTime = "2020/01/01 00:00:00",
                lastUpdateTime = "2023-02-02 22:33:44"
            ),
            changeGachaId = {},
            changeShowResult = {},
            changeSelect = {},
            updatePickUpList = {},
            deleteGachaByGachaId = {}
        )
    }
}