package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.data.db.entity.UnitPromotion
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.MainCard
import cn.wthee.pcrtool.ui.compose.RankText
import cn.wthee.pcrtool.ui.compose.getEquipIconUrl
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

/**
 * 角色各 RANK 装备列表
 */
@ExperimentalFoundationApi
@Composable
fun RankEquipList(
    unitId: Int,
    toEquipDetail: (Int) -> Unit,
    navViewModel: NavViewModel,
    equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel(),
) {
    equipmentViewModel.getAllRankEquipList(unitId)
    val allRankEquip = equipmentViewModel.allRankEquipList.observeAsState().value ?: arrayListOf()
    val selectedRank = remember {
        mutableStateOf(navViewModel.selectRank.value ?: 2)
    }

    if (allRankEquip.isNotEmpty()) {
        val spanCount = 3
        LazyVerticalGrid(cells = GridCells.Fixed(spanCount)) {
            items(allRankEquip) {
                RankEquipListItem(it, selectedRank, toEquipDetail, navViewModel)
            }
        }
    }
}

/**
 * RANK 装备图标列表
 */
@Composable
fun RankEquipListItem(
    unitPromotion: UnitPromotion,
    selectedRank: MutableState<Int>,
    toEquipDetail: (Int) -> Unit,
    navViewModel: NavViewModel,
) {
    val color = if (unitPromotion.promotionLevel == selectedRank.value)
        MaterialTheme.colors.primary
    else
        MaterialTheme.colors.surface

    MainCard(
        modifier = Modifier.padding(Dimen.mediuPadding),
        onClick = {
            selectedRank.value = unitPromotion.promotionLevel
            navViewModel.selectRank.postValue(unitPromotion.promotionLevel)
        }
    ) {
        //图标列表
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color, Shapes.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //RANK
            RankText(
                rank = unitPromotion.promotionLevel,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(Dimen.mediuPadding)
            )
            val allIds = unitPromotion.getAllOrderIds()
            allIds.forEachIndexed { index, _ ->
                if (index % 2 == 0) {
                    Row(horizontalArrangement = Arrangement.SpaceAround) {
                        IconCompose(
                            data = getEquipIconUrl(allIds[index]),
                            modifier = Modifier.padding(Dimen.smallPadding)
                        ) {
                            toEquipDetail(allIds[index])
                        }
                        IconCompose(
                            data = getEquipIconUrl(allIds[index + 1]),
                            modifier = Modifier.padding(Dimen.smallPadding)
                        ) {
                            toEquipDetail(allIds[index + 1])
                        }
                    }
                }
            }
        }
    }

}