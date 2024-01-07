package cn.wthee.pcrtool.ui.home.uniqueequip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.ui.components.GridIconList
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.dp2px
import cn.wthee.pcrtool.utils.spanCount


/**
 * 专用装备预览
 */
@Composable
fun UniqueEquipSection(
    updateOrderData: (Int) -> Unit,
    toUniqueEquipList: () -> Unit,
    toUniqueEquipDetail: (Int) -> Unit,
    isEditMode: Boolean,
    orderStr: String,
    uniqueEquipSectionViewModel: UniqueEquipSectionViewModel = hiltViewModel()
) {
    val id = OverviewType.UNIQUE_EQUIP.id
    val uiState by uniqueEquipSectionViewModel.uiState.collectAsStateWithLifecycle()

    val equipSpanCount = spanCount(
        LocalView.current.width - (Dimen.mediumPadding * 2).value.dp2px,
        Dimen.homeIconItemWidth
    )

    LaunchedEffect(equipSpanCount) {
        uniqueEquipSectionViewModel.loadData(equipSpanCount)
    }


    Section(
        id = id,
        titleId = R.string.tool_unique_equip,
        iconType = MainIconType.UNIQUE_EQUIP,
        hintText = uiState.uniqueEquipCount,
        contentVisible = uiState.uniqueEquipCount != "0",
        isEditMode = isEditMode,
        orderStr = orderStr,
        onClick = {
            if (isEditMode) {
                updateOrderData(id)
            } else {
                toUniqueEquipList()
            }
        }
    ) {
        uiState.uniqueEquipList1?.let { list ->
            GridIconList(
                idList = list.map { it.equipId },
                detailIdList = list.map { it.unitId },
                iconResourceType = IconResourceType.UNIQUE_EQUIP,
                itemWidth = Dimen.homeIconItemWidth,
                contentPadding = 0.dp,
                onClickItem = toUniqueEquipDetail
            )
        }
        uiState.uniqueEquipList2?.let { list ->
            GridIconList(
                idList = list.map { it.equipId },
                detailIdList = list.map { it.unitId },
                iconResourceType = IconResourceType.UNIQUE_EQUIP,
                itemWidth = Dimen.homeIconItemWidth,
                contentPadding = 0.dp,
                onClickItem = toUniqueEquipDetail
            )
        }
    }
}