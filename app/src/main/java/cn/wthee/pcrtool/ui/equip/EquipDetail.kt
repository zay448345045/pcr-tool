package cn.wthee.pcrtool.ui.equip

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.components.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel


/**
 * 装备详情
 *
 * @param equipId 装备编号
 */
@Composable
fun EquipMainInfo(
    equipId: Int,
    toEquipMaterial: (Int) -> Unit,
    toEquipUnit: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val equipMaxData =
        equipmentViewModel.getEquip(equipId).collectAsState(initial = EquipmentMaxData()).value
    val materialList =
        equipmentViewModel.getEquipInfos(equipMaxData).collectAsState(initial = arrayListOf()).value
    val starIds = FilterEquipment.getStarIdList()
    val loved = remember {
        mutableStateOf(starIds.contains(equipId))
    }
    val unitIds = equipmentViewModel.getEquipUnitList(equipId)
        .collectAsState(initial = arrayListOf()).value


    EquipDetail(equipId, unitIds, equipMaxData, materialList, loved, toEquipMaterial, toEquipUnit)
}

@Composable
private fun EquipDetail(
    equipId: Int,
    unitIds: List<Int>,
    equipMaxData: EquipmentMaxData,
    materialList: ArrayList<EquipmentMaterial>,
    loved: MutableState<Boolean>,
    toEquipMaterial: (Int) -> Unit,
    toEquipUnit: (Int) -> Unit,
) {
    val text = if (loved.value) "" else stringResource(id = R.string.love_equip)

    Box(
        modifier = Modifier
            .padding(top = Dimen.largePadding)
            .fillMaxSize()
    ) {

        Column {
            if (equipId != UNKNOWN_EQUIP_ID) {
                if (BuildConfig.DEBUG) {
                    Subtitle1(
                        text = equipId.toString(),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                }
                MainText(
                    text = equipMaxData.equipmentName,
                    color = if (loved.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    selectable = true
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.largePadding)
                ) {
                    MainIcon(
                        data = ImageRequestHelper.getInstance().getEquipPic(equipId)
                    )
                    Subtitle2(
                        text = equipMaxData.getDesc(),
                        modifier = Modifier.padding(start = Dimen.mediumPadding),
                        selectable = true
                    )
                }
                //属性
                AttrList(attrs = equipMaxData.attr.allNotZero(isPreview = LocalInspectionMode.current))

            }
            //合成素材
            EquipMaterialList(materialList, toEquipMaterial)
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin),
            horizontalArrangement = Arrangement.End
        ) {
            //装备收藏
            MainSmallFab(
                iconType = if (loved.value) MainIconType.LOVE_FILL else MainIconType.LOVE_LINE,
                text = text
            ) {
                FilterEquipment.addOrRemove(equipId)
                loved.value = !loved.value
            }

            //关联角色
            if(unitIds.isNotEmpty()){
                MainSmallFab(
                    iconType = MainIconType.CHARACTER,
                    text = unitIds.size.toString()
                ) {
                    toEquipUnit(equipId)
                }
            }

        }

    }
}

/**
 * 装备合成素材
 *
 * @param materialList 装备素材信息
 */
@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun EquipMaterialList(
    materialList: ArrayList<EquipmentMaterial>,
    toEquipMaterial: (Int) -> Unit
) {
    val starIds = remember {
        mutableStateOf(arrayListOf<Int>())
    }
    if (!LocalInspectionMode.current) {
        LaunchedEffect(MainActivity.navSheetState.isVisible) {
            starIds.value = FilterEquipment.getStarIdList()
        }
    }


    Column(modifier = Modifier.padding(horizontal = Dimen.commonItemPadding)) {
        MainText(
            text = stringResource(R.string.equip_material),
            modifier = Modifier
                .padding(top = Dimen.largePadding, bottom = Dimen.mediumPadding)
                .align(Alignment.CenterHorizontally)
        )
        //装备合成素材
        VerticalGrid(itemWidth = Dimen.iconSize, contentPadding = Dimen.largePadding) {
            materialList.forEach { material ->
                val loved = starIds.value.contains(material.id)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = Dimen.largePadding
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MainIcon(
                        data = ImageRequestHelper.getInstance().getEquipPic(material.id)
                    ) {
                        toEquipMaterial(material.id)
                    }
                    SelectText(
                        selected = loved,
                        text = material.count.toString()
                    )
                }
            }
        }
    }
}

/**
 * 可使用装备角色列表
 */
@Composable
fun EquipUnitList(
    equipId: Int,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val unitIds = equipmentViewModel.getEquipUnitList(equipId)
        .collectAsState(initial = arrayListOf()).value

    UnitList(unitIds)
}

@CombinedPreviews
@Composable
private fun EquipDetailPreview() {
    val loved = remember {
        mutableStateOf(true)
    }
    PreviewLayout {
        EquipDetail(
            equipId = 0,
            arrayListOf(),
            equipMaxData = EquipmentMaxData(1001, "?", "", "?", 1, attr = Attr().random()),
            materialList = arrayListOf(EquipmentMaterial()),
            loved = loved,
            toEquipMaterial = {},
            {})
    }
}