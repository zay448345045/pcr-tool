package cn.wthee.pcrtool.ui.character.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.db.view.UnitStatusCoefficient
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.enums.CharacterDetailModuleType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RankColor
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.enums.VideoType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.navigation.NavActions
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.navigation.getData
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.character.CharacterItemContent
import cn.wthee.pcrtool.ui.character.CharacterItemPreview
import cn.wthee.pcrtool.ui.character.skillloop.CharacterSkillLoopScreen
import cn.wthee.pcrtool.ui.components.AttrList
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.LevelInputText
import cn.wthee.pcrtool.ui.components.LifecycleEffect
import cn.wthee.pcrtool.ui.components.MainAlertDialog
import cn.wthee.pcrtool.ui.components.MainHorizontalPagerIndicator
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.SubButton
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.home.Section
import cn.wthee.pcrtool.ui.skill.SkillListScreen
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.tool.uniqueequip.UniqueEquipDetail
import cn.wthee.pcrtool.ui.tool.uniqueequip.UnitIconAndTag
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.getFormatText
import cn.wthee.pcrtool.utils.int


/**
 * 角色信息
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CharacterDetailScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    actions: NavActions,
    characterDetailViewModel: CharacterDetailViewModel = hiltViewModel()
) {
    val uiState by characterDetailViewModel.uiState.collectAsStateWithLifecycle()

    //rank 装备选择监听
    LifecycleEffect(Lifecycle.Event.ON_RESUME) {
        val currentRank = getData<Int>(NavRoute.RANK)
        if (currentRank != null) {
            characterDetailViewModel.updateCurrentValue(uiState.currentValue.copy(rank = currentRank))
        }
    }

    //页面状态
    val pagerState = rememberPagerState { uiState.pageCount }

    MainScaffold(
        fab = {
            CharacterDetailFabContent(
                loadState = uiState.loadState,
                currentId = uiState.currentId,
                showAllInfo = uiState.showAllInfo,
                isEditMode = uiState.isEditMode,
                favorite = uiState.favorite,
                orderData = uiState.orderData,
                changeEditMode = characterDetailViewModel::changeEditMode,
                updateFavoriteCharacterId = characterDetailViewModel::updateFavoriteId,
                toCharacterDetail = actions.toCharacterDetail,
                toCharacterSkillLoop = actions.toCharacterSkillLoop,
            )
        },
        secondLineFab = {
            ChangeCutinFabContent(
                loadState = uiState.loadState,
                cutinId = uiState.cutinId,
                showAllInfo = uiState.showAllInfo,
                isCutinSkill = uiState.isCutinSkill,
                changeCutin = characterDetailViewModel::changeCutin
            )
        },
        mainFabIcon = if (uiState.isEditMode) MainIconType.OK else MainIconType.BACK,
        onMainFabClick = {
            if (uiState.isEditMode) {
                characterDetailViewModel.changeEditMode(false)
            } else {
                navigateUp()
            }
        }
    ) {
        StateBox(
            stateType = uiState.loadState,
            errorContent = {
                //未登场角色
                CenterTipText(text = stringResource(R.string.unknown_character))
            }
        ) {
            CharacterDetailContent(
                animatedVisibilityScope = animatedVisibilityScope,
                uiState = uiState,
                pagerState = pagerState,
                updateOrderData = characterDetailViewModel::updateOrderData,
                updateCurrentValue = characterDetailViewModel::updateCurrentValue,
                actions = actions
            )
        }

        //页面指示器
        if (uiState.pageCount == 2 && !uiState.isEditMode && uiState.loadState == LoadState.Success) {
            MainHorizontalPagerIndicator(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = Dimen.exSmallPadding,
                        bottom = Dimen.fabSize / 2 + Dimen.fabMargin
                    ),
                pagerState = pagerState,
                pageCount = uiState.pageCount
            )
        }
    }

}

/**
 * 悬浮按钮
 * 收藏、编辑、技能循环、技能形态、角色详情
 */
@Composable
private fun CharacterDetailFabContent(
    loadState: LoadState,
    currentId: Int,
    showAllInfo: Boolean,
    isEditMode: Boolean,
    favorite: Boolean,
    orderData: String,
    changeEditMode: (Boolean) -> Unit,
    updateFavoriteCharacterId: () -> Unit,
    toCharacterSkillLoop: (Int) -> Unit,
    toCharacterDetail: (Int) -> Unit,
) {
    if (loadState == LoadState.Success) {
        if (showAllInfo) {
            if (!isEditMode) {
                //编辑
                MainSmallFab(
                    iconType = MainIconType.EDIT_TOOL,
                    onClick = {
                        changeEditMode(true)
                    }
                )

                //收藏
                MainSmallFab(
                    iconType = if (favorite) {
                        MainIconType.FAVORITE_FILL
                    } else {
                        MainIconType.FAVORITE_LINE
                    },
                    onClick = {
                        updateFavoriteCharacterId()
                    }
                )
            }

            //技能循环
            if (!orderData.contains(CharacterDetailModuleType.SKILL_LOOP.id.toString())) {
                MainSmallFab(
                    iconType = MainIconType.SKILL_LOOP,
                    onClick = {
                        if (!isEditMode) {
                            toCharacterSkillLoop(currentId)
                        }
                    }
                )
            }

        } else {
            //切换详情，专用装备跳转过来时，显示该按钮
            MainSmallFab(
                iconType = MainIconType.CHARACTER,
                text = stringResource(id = R.string.character_detail),
                onClick = {
                    toCharacterDetail(currentId)
                }
            )
        }
    }
}

@Composable
private fun ChangeCutinFabContent(
    loadState: LoadState,
    cutinId: Int,
    showAllInfo: Boolean,
    isCutinSkill: Boolean,
    changeCutin: () -> Unit,
) {
    if (loadState == LoadState.Success && cutinId != 0 && showAllInfo) {
        //角色技能形态
        MainSmallFab(
            modifier = Modifier.padding(
                end = Dimen.fabMargin,
                bottom = Dimen.fabMarginLargeBottom
            ),
            iconType = if (isCutinSkill) {
                MainIconType.CHARACTER_CUTIN_SKILL
            } else {
                MainIconType.CHARACTER_NORMAL_SKILL
            },
            text = if (isCutinSkill) {
                stringResource(id = R.string.cutin_skill)
            } else {
                ""
            },
            onClick = {
                changeCutin()
            }
        )
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.CharacterDetailContent(
    animatedVisibilityScope: AnimatedVisibilityScope,
    uiState: CharacterDetailUiState,
    pagerState: PagerState,
    actions: NavActions,
    updateOrderData: (Int) -> Unit,
    updateCurrentValue: (CharacterProperty) -> Unit,
) {

    val scrollState0 = rememberScrollState()
    val scrollState1 = rememberScrollState()


    if (uiState.isEditMode) {
        //编辑模式
        val typeList = arrayListOf(
            CharacterDetailModuleType.CARD,
            CharacterDetailModuleType.COE,
            CharacterDetailModuleType.TOOLS,
            CharacterDetailModuleType.STAR,
            CharacterDetailModuleType.LEVEL,
            CharacterDetailModuleType.ATTR,
            CharacterDetailModuleType.OTHER_TOOLS,
            CharacterDetailModuleType.EQUIP,
            CharacterDetailModuleType.UNIQUE_EQUIP,
            CharacterDetailModuleType.SKILL_LOOP,
            CharacterDetailModuleType.SKILL,
            CharacterDetailModuleType.UNIT_ICON,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 编辑模式
            Subtitle2(
                text = stringResource(R.string.order_character_detail),
                modifier = Modifier
                    .padding(vertical = Dimen.mediumPadding)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            typeList.forEach {
                Section(
                    id = it.id,
                    titleId = it.titleId,
                    isEditMode = true,
                    orderStr = uiState.orderData,
                    onClick = {
                        updateOrderData(it.id)
                    }
                ) {}
            }

            CommonSpacer()
            CommonSpacer()
        }

    } else {
        //正常模式
        HorizontalPager(state = pagerState) { index ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(if (index == 0) scrollState0 else scrollState1),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val list = if (index == 0) {
                    uiState.mainList
                } else {
                    uiState.subList
                }
                list.forEach {
                    when (CharacterDetailModuleType.getByValue(it)) {

                        //角色卡面
                        CharacterDetailModuleType.CARD -> CharacterCard(
                            animatedVisibilityScope = animatedVisibilityScope,
                            unitId = uiState.unitId,
                            characterInfo = uiState.characterInfo,
                            favorite = uiState.favorite,
                            toAllPics = actions.toAllPics
                        )

                        //战力
                        CharacterDetailModuleType.COE ->
                            CharacterCoeContent(
                                coeValue = uiState.coeValue,
                                allAttr = uiState.allAttr,
                                currentValue = uiState.currentValue,
                                toCoe = actions.toCoe
                            )

                        //资料
                        CharacterDetailModuleType.TOOLS ->
                            ToolsContent(
                                unitId = uiState.unitId,
                                cutinId = uiState.cutinId,
                                talentType = uiState.characterInfo?.talentId ?: 0,
                                idList = uiState.idList,
                                toCharacterBasicInfo = actions.toCharacterBasicInfo,
                                toAllPics = actions.toAllPics,
                                toCharacterVideo = actions.toCharacterVideo,
                                toUnitTalentFilterList = actions.toUnitTalentFilterList
                            )

                        //星级
                        CharacterDetailModuleType.STAR -> StarSelectContent(
                            currentValue = uiState.currentValue,
                            max = uiState.maxValue.rarity,
                            updateCurrentValue = updateCurrentValue
                        )

                        //等级
                        CharacterDetailModuleType.LEVEL -> LevelInputText(
                            text = uiState.currentValue.level.toString(),
                            maxLevel = uiState.maxValue.level,
                            onDone = { level ->
                                updateCurrentValue(uiState.currentValue.copy(level = level))
                            }
                        )

                        //属性
                        CharacterDetailModuleType.ATTR ->
                            AttrListContent(
                                unitId = uiState.unitId,
                                allAttr = uiState.allAttr,
                                toCharacterStoryDetail = actions.toCharacterStoryDetail
                            )

                        //其他功能
                        CharacterDetailModuleType.OTHER_TOOLS -> OtherToolsContent(
                            unitId = uiState.unitId,
                            currentValue = uiState.currentValue,
                            maxRank = uiState.maxValue.rank,
                            toCharacterRankCompare = actions.toCharacterRankCompare,
                            toCharacterEquipCount = actions.toCharacterEquipCount,
                            toCharacterExtraEquip = actions.toCharacterExtraEquip
                        )

                        //装备
                        CharacterDetailModuleType.EQUIP -> EquipContent(
                            rank = uiState.currentValue.rank,
                            unitId = uiState.unitId,
                            currentValue = uiState.currentValue,
                            maxRank = uiState.maxValue.rank,
                            equips = uiState.allAttr.equips,
                            updateCurrentValue = updateCurrentValue,
                            toEquipDetail = actions.toEquipDetail,
                            toCharacterRankEquip = actions.toCharacterRankEquip
                        )

                        //专武
                        CharacterDetailModuleType.UNIQUE_EQUIP -> uiState.allAttr.uniqueEquipList
                            .forEachIndexed { index, uniqueEquipmentMaxData ->
                                UniqueEquipDetail(
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    slot = index + 1,
                                    currentValue = uiState.currentValue,
                                    uniqueEquipLevelMax = if (index == 0) {
                                        uiState.maxValue.uniqueEquipmentLevel
                                    } else {
                                        5
                                    },
                                    uniqueEquipmentMaxData = uniqueEquipmentMaxData,
                                    updateCurrentValue = updateCurrentValue,
                                )
                            }

                        //技能列表
                        CharacterDetailModuleType.SKILL -> SkillListScreen(
                            unitId = uiState.currentId,
                            atk = uiState.maxAtk,
                            unitType = UnitType.CHARACTER,
                            property = uiState.currentValue,
                            toSummonDetail = actions.toSummonDetail,
                            toCharacterVideo = actions.toCharacterVideo,
                            isFilterSkill = !uiState.showAllInfo,
                            filterSkillCount = uiState.allAttr.uniqueEquipList.size,
                        )

                        //图标
                        CharacterDetailModuleType.UNIT_ICON -> {
                            UnitIconAndTag(
                                characterInfo = uiState.characterInfo,
                                showUniqueEquipType = uiState.showAllInfo,
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                        }

                        //技能循环
                        CharacterDetailModuleType.SKILL_LOOP -> CharacterSkillLoopScreen(
                            unitId = uiState.currentId,
                            scrollable = false
                        )

                        CharacterDetailModuleType.UNKNOWN -> {
                            CenterTipText(stringResource(id = R.string.unknown))
                        }
                    }
                }

                CommonSpacer()
                CommonSpacer()
            }
        }
    }
}


/**
 * 角色卡面
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.CharacterCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    unitId: Int,
    characterInfo: CharacterInfo?,
    favorite: Boolean,
    toAllPics: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(
                top = Dimen.largePadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding,
                bottom = Dimen.smallPadding,
            )
            .widthIn(max = Dimen.itemMaxWidth)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //卡面信息
        CharacterItemContent(
            animatedVisibilityScope = animatedVisibilityScope,
            unitId = unitId,
            characterInfo = characterInfo,
            favorite = favorite,
            onClick = {
                toAllPics(unitId, AllPicsType.CHARACTER.type)
            }
        )
    }

}

/**
 * 角色功能
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ToolsContent(
    unitId: Int,
    cutinId: Int,
    talentType: Int,
    idList: ArrayList<Int>,
    toCharacterBasicInfo: (Int) -> Unit,
    toAllPics: (Int, Int) -> Unit,
    toCharacterVideo: (Int, Int) -> Unit,
    toUnitTalentFilterList: (Int, Int) -> Unit,
) {
    val openDialog = remember {
        mutableStateOf(false)
    }

    FlowRow(
        modifier = Modifier
            .padding(horizontal = Dimen.largePadding),
        horizontalArrangement = Arrangement.Center
    ) {

        //资料
        IconTextButton(
            icon = MainIconType.CHARACTER_INTRO,
            text = stringResource(id = R.string.character_basic_info),
            onClick = {
                toCharacterBasicInfo(unitId)
            }
        )
        //立绘预览
        IconTextButton(
            icon = MainIconType.PREVIEW_IMAGE,
            text = stringResource(id = R.string.character_pic),
            modifier = Modifier.padding(end = Dimen.smallPadding),
            onClick = {
                toAllPics(unitId, AllPicsType.CHARACTER.type)
            }
        )
        //模型预览
        IconTextButton(
            icon = MainIconType.PREVIEW_UNIT_SPINE,
            text = stringResource(id = R.string.spine_preview),
            onClick = {
                if (idList.size > 1) {
                    //弹窗选择
                    openDialog.value = true
                } else {
                    val id = if (cutinId != 0) cutinId else unitId
                    BrowserUtil.open(Constants.PREVIEW_UNIT_URL + id)
                }
            }
        )
        //动态卡面
        IconTextButton(
            icon = MainIconType.MOVIE,
            text = stringResource(id = R.string.character_card_video),
            modifier = Modifier.padding(end = Dimen.smallPadding),
            onClick = {
                toCharacterVideo(unitId, VideoType.CHARACTER_CARD.value)
            }
        )
        //相同天赋角色
        IconTextButton(
            icon = MainIconType.TALENT,
            text = stringResource(id = R.string.talent),
            modifier = Modifier.padding(end = Dimen.smallPadding),
            onClick = {
                toUnitTalentFilterList(unitId, talentType)
            }
        )
    }

    if (openDialog.value) {
        SpineTypeSelectDialog(openDialog = openDialog, idList = idList)
    }
}

/**
 * 多人角色预览模型类型选择弹窗
 */
@Composable
private fun SpineTypeSelectDialog(
    openDialog: MutableState<Boolean>,
    idList: List<Int>
) {
    MainAlertDialog(
        showButton = false,
        openDialog = openDialog,
        icon = MainIconType.PREVIEW_UNIT_SPINE,
        title = stringResource(id = R.string.preview_spine_type),
        content = {
            Column {
                //战斗
                Row(
                    modifier = Modifier.padding(top = Dimen.largePadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.preview_spine_battle),
                        modifier = Modifier.padding(end = Dimen.largePadding)
                    )
                    MainIcon(
                        data = ImageRequestHelper.getInstance().getMaxIconUrl(idList[0]),
                        onClick = {
                            BrowserUtil.open(Constants.PREVIEW_UNIT_URL + idList[0] + "&type=1")
                        }
                    )
                }

                //小屋
                Row(
                    modifier = Modifier.padding(top = Dimen.largePadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.preview_spine_room),
                        modifier = Modifier.padding(end = Dimen.largePadding)
                    )
                    idList.subList(1, idList.size).forEach {
                        MainIcon(
                            data = ImageRequestHelper.getInstance().getMaxIconUrl(it),
                            onClick = {
                                BrowserUtil.open(Constants.PREVIEW_UNIT_URL + it + "&type=2")
                            },
                            modifier = Modifier.padding(end = Dimen.largePadding)
                        )
                    }
                }

            }
        }
    )
}

/**
 * 其他功能
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OtherToolsContent(
    unitId: Int,
    currentValue: CharacterProperty,
    maxRank: Int,
    toCharacterRankCompare: (Int, Int, Int, Int, Int, Int) -> Unit,
    toCharacterEquipCount: (Int) -> Unit,
    toCharacterExtraEquip: (Int) -> Unit
) {

    //RANK相关功能
    FlowRow(
        modifier = Modifier
            .padding(horizontal = Dimen.largePadding),
        horizontalArrangement = Arrangement.Center
    ) {
        //RANK对比
        IconTextButton(
            icon = MainIconType.RANK_COMPARE,
            text = stringResource(id = R.string.rank_compare),
            onClick = {
                toCharacterRankCompare(
                    unitId,
                    maxRank,
                    currentValue.level,
                    currentValue.rarity,
                    currentValue.uniqueEquipmentLevel,
                    currentValue.uniqueEquipmentLevel2,
                )
            }
        )
        //装备统计
        IconTextButton(
            icon = MainIconType.EQUIP_CALC,
            text = stringResource(id = R.string.calc_equip_count),
            onClick = {
                toCharacterEquipCount(unitId)
            }
        )
        //ex装备
        IconTextButton(
            icon = MainIconType.EXTRA_EQUIP,
            text = stringResource(id = R.string.tool_extra_equip),
            onClick = {
                toCharacterExtraEquip(unitId)
            }
        )
    }
}

/**
 * 属性
 */
@Composable
private fun ColumnScope.AttrListContent(
    unitId: Int,
    allAttr: AllAttrData,
    toCharacterStoryDetail: (Int) -> Unit
) {
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(Dimen.largePadding))
    //属性
    AttrList(attrs = allAttr.sumAttr.all(context))
    //剧情属性
    val storyAttrList =
        allAttr.storyAttr.allNotZero(context)
    if (storyAttrList.isNotEmpty()) {
        Row(
            modifier = Modifier
                .padding(
                    top = Dimen.largePadding, bottom = Dimen.smallPadding
                )
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable {
                    VibrateUtil(context).single()
                    toCharacterStoryDetail(unitId)
                }
                .padding(horizontal = Dimen.smallPadding)
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            MainText(text = stringResource(id = R.string.title_story_attr))
            MainIcon(
                data = MainIconType.HELP, size = Dimen.smallIconSize
            )
        }
        AttrList(attrs = storyAttrList)
    }
    //Rank 奖励
    val rankBonusList =
        allAttr.rankBonus.attr.allNotZero(context)
    if (rankBonusList.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainText(
                text = stringResource(id = R.string.title_rank_bonus),
                modifier = Modifier.Companion.padding(
                    top = Dimen.largePadding, bottom = Dimen.smallPadding
                ),
                textAlign = TextAlign.Center
            )
            AttrList(attrs = rankBonusList)
        }
    }
    Spacer(modifier = Modifier.height(Dimen.largePadding))
}


/**
 * 战力计算
 */
@Composable
private fun CharacterCoeContent(
    coeValue: UnitStatusCoefficient?,
    allAttr: AllAttrData,
    currentValue: CharacterProperty,
    toCoe: () -> Unit
) {
    val context = LocalContext.current


    Row(
        modifier = Modifier
            .padding(start = Dimen.smallPadding)
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable {
                VibrateUtil(context).single()
                toCoe()
            }
            .padding(horizontal = Dimen.smallPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var value = ""

        coeValue?.let { coe ->
            val basicAttr = allAttr.sumAttr.copy().sub(allAttr.exSkillAttr)
            val basic =
                basicAttr.hp * coe.hp_coefficient + basicAttr.atk * coe.atk_coefficient + basicAttr.magicStr * coe.magic_str_coefficient + basicAttr.def * coe.def_coefficient + basicAttr.magicDef * coe.magic_def_coefficient + basicAttr.physicalCritical * coe.physical_critical_coefficient + basicAttr.magicCritical * coe.magic_critical_coefficient + basicAttr.waveHpRecovery * coe.wave_hp_recovery_coefficient + basicAttr.waveEnergyRecovery * coe.wave_energy_recovery_coefficient + basicAttr.dodge * coe.dodge_coefficient + basicAttr.physicalPenetrate * coe.physical_penetrate_coefficient + basicAttr.magicPenetrate * coe.magic_penetrate_coefficient + basicAttr.lifeSteal * coe.life_steal_coefficient + basicAttr.hpRecoveryRate * coe.hp_recovery_rate_coefficient + basicAttr.energyRecoveryRate * coe.energy_recovery_rate_coefficient + basicAttr.energyReduceRate * coe.energy_reduce_rate_coefficient + basicAttr.accuracy * coe.accuracy_coefficient
            //技能2：默认加上技能2
            var skill = currentValue.level * coe.skill_lv_coefficient
            //技能1：解锁专武，技能1系数提升
            if (allAttr.uniqueEquipList.isNotEmpty()) {
                skill += coe.skill1_evolution_coefficient * allAttr.uniqueEquipList.size
                skill += currentValue.level * coe.skill_lv_coefficient * coe.skill1_evolution_slv_coefficient * allAttr.uniqueEquipList.size
            } else {
                skill += currentValue.level * coe.skill_lv_coefficient
            }
            //不同星级处理
            if (currentValue.rarity >= 5) {
                //ex+:大于等于五星，技能 ex+
                skill += coe.exskill_evolution_coefficient
                skill += currentValue.level * coe.skill_lv_coefficient
                if (currentValue.rarity == 6) {
                    //ub+
                    skill += coe.ub_evolution_coefficient
                    skill += currentValue.level * coe.skill_lv_coefficient * coe.ub_evolution_slv_coefficient
                } else {
                    //ub
                    skill += currentValue.level * coe.skill_lv_coefficient
                }
            } else {
                //ub、ex
                skill += currentValue.level * coe.skill_lv_coefficient * 2
            }
            value = (basic + skill).int.toString()
        }
        //战力数值
        MainText(
            text = stringResource(id = R.string.attr_all_value, value),
        )
        MainIcon(
            data = MainIconType.HELP, size = Dimen.smallIconSize
        )
    }
}

/**
 * 角色 RANK 装备
 */
@Composable
private fun EquipContent(
    unitId: Int,
    rank: Int,
    maxRank: Int,
    currentValue: CharacterProperty,
    equips: List<EquipmentMaxData>,
    updateCurrentValue: (CharacterProperty) -> Unit,
    toEquipDetail: (Int) -> Unit,
    toCharacterRankEquip: (Int, Int) -> Unit
) {

    Column(
        modifier = Modifier
            .padding(vertical = Dimen.largePadding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //装备 6、 3
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(Dimen.iconSize * 4)
        ) {
            val id6 = equips[0].equipmentId
            val id3 = equips[1].equipmentId
            MainIcon(
                data = ImageRequestHelper.getInstance()
                    .getUrl(ImageRequestHelper.ICON_EQUIPMENT, id6),
                onClick = {
                    if (id6 != UNKNOWN_EQUIP_ID) {
                        toEquipDetail(id6)
                    }
                }
            )
            MainIcon(
                data = ImageRequestHelper.getInstance()
                    .getUrl(ImageRequestHelper.ICON_EQUIPMENT, id3),
                onClick = {
                    if (id3 != UNKNOWN_EQUIP_ID) {
                        toEquipDetail(id3)
                    }
                }
            )
        }
        //装备 5、 2
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.mediumPadding)
        ) {
            val id5 = equips[2].equipmentId
            MainIcon(
                data = ImageRequestHelper.getInstance()
                    .getUrl(ImageRequestHelper.ICON_EQUIPMENT, id5),
                onClick = {
                    if (id5 != UNKNOWN_EQUIP_ID) {
                        toEquipDetail(id5)
                    }
                }
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentValue.rank < maxRank) {
                    MainIcon(
                        data = MainIconType.BACK,
                        tint = RankColor.getRankColor(rank = rank + 1),
                        onClick = {
                            updateCurrentValue(currentValue.copy(rank = rank + 1))
                        },
                        size = Dimen.mediumIconSize,
                        modifier = Modifier.padding(start = Dimen.mediumPadding)
                    )
                } else {
                    Spacer(modifier = Modifier.width(Dimen.mediumPadding + Dimen.mediumIconSize))
                }
                //跳转至所有 RANK 装备列表
                SubButton(
                    text = getFormatText(rank),
                    color = RankColor.getRankColor(rank),
                    modifier = Modifier.padding(
                        vertical = Dimen.largePadding * 2
                    ),
                    useBrush = true,
                    onClick = {
                        toCharacterRankEquip(unitId, rank)
                    }
                )
                if (rank > 1) {
                    MainIcon(
                        data = MainIconType.MORE,
                        tint = RankColor.getRankColor(rank = rank - 1),
                        onClick = {
                            updateCurrentValue(currentValue.copy(rank = rank - 1))
                        },
                        size = Dimen.mediumIconSize,
                        modifier = Modifier.padding(end = Dimen.mediumPadding)
                    )
                } else {
                    Spacer(modifier = Modifier.width(Dimen.mediumPadding + Dimen.mediumIconSize))
                }
            }
            val id2 = equips[3].equipmentId
            MainIcon(
                data = ImageRequestHelper.getInstance()
                    .getUrl(ImageRequestHelper.ICON_EQUIPMENT, id2),
                onClick = {
                    if (id2 != UNKNOWN_EQUIP_ID) {
                        toEquipDetail(id2)
                    }
                }
            )
        }
        //装备 4、 1
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(Dimen.iconSize * 4)
        ) {
            val id4 = equips[4].equipmentId
            val id1 = equips[5].equipmentId
            MainIcon(
                data = ImageRequestHelper.getInstance()
                    .getUrl(ImageRequestHelper.ICON_EQUIPMENT, id4),
                onClick = {
                    if (id4 != UNKNOWN_EQUIP_ID) {
                        toEquipDetail(id4)
                    }
                }
            )
            MainIcon(
                data = ImageRequestHelper.getInstance()
                    .getUrl(ImageRequestHelper.ICON_EQUIPMENT, id1),
                onClick = {
                    if (id1 != UNKNOWN_EQUIP_ID) {
                        toEquipDetail(id1)
                    }
                }
            )
        }
    }
}


/**
 * 星级选择
 * @param max 最大值
 */
@Composable
private fun StarSelectContent(
    currentValue: CharacterProperty,
    max: Int,
    updateCurrentValue: (CharacterProperty) -> Unit
) {

    Row(modifier = Modifier.padding(Dimen.mediumPadding)) {
        for (i in 1..max) {
            val iconId = when {
                i > currentValue.rarity -> R.drawable.ic_star_empty
                i == 6 -> R.drawable.ic_star_pink
                else -> R.drawable.ic_star
            }
            MainIcon(
                data = iconId,
                size = Dimen.textIconSize,
                modifier = Modifier.padding(Dimen.smallPadding),
                onClick = {
                    updateCurrentValue(currentValue.copy(rarity = i))
                }
            )

        }
    }
}

@CombinedPreviews
@Composable
private fun FabContentPreview() {
    PreviewLayout {
        Row {
            CharacterDetailFabContent(
                loadState = LoadState.Success,
                currentId = 101001,
                showAllInfo = true,
                isEditMode = false,
                favorite = true,
                orderData = "",
                changeEditMode = {},
                updateFavoriteCharacterId = {},
                toCharacterSkillLoop = {},
                toCharacterDetail = {},
            )
        }
    }
}

@CombinedPreviews
@Composable
private fun CharacterCardPreview() {
    CharacterItemPreview()
}

@CombinedPreviews
@Composable
private fun ToolsContentPreview() {
    PreviewLayout {
        ToolsContent(
            unitId = 100101,
            cutinId = 0,
            talentType = 0,
            idList = arrayListOf(),
            toCharacterBasicInfo = {},
            toAllPics = { _, _ -> },
            toCharacterVideo = { _, _ -> },
            toUnitTalentFilterList = { _, _ -> },
        )
    }
}

@CombinedPreviews
@Composable
private fun OtherToolsContentPreview() {
    PreviewLayout {
        OtherToolsContent(
            unitId = 100101,
            currentValue = CharacterProperty(),
            maxRank = 1,
            toCharacterRankCompare = { _, _, _, _, _, _ -> },
            toCharacterEquipCount = {},
            toCharacterExtraEquip = {}
        )
    }
}


@CombinedPreviews
@Composable
private fun AttrListContentPreview() {
    PreviewLayout {
        AttrListContent(
            unitId = 101001,
            allAttr = AllAttrData(),
            toCharacterStoryDetail = { },
        )
    }
}

@CombinedPreviews
@Composable
private fun CharacterCoeContentPreview() {
    PreviewLayout {
        CharacterCoeContent(
            coeValue = UnitStatusCoefficient(),
            allAttr = AllAttrData(),
            currentValue = CharacterProperty(),
            toCoe = { },
        )
    }
}

@CombinedPreviews
@Composable
private fun EquipContentPreview() {
    PreviewLayout {
        EquipContent(
            rank = 1,
            unitId = 101001,
            currentValue = CharacterProperty(),
            maxRank = 22,
            equips = arrayListOf(
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData(),
                EquipmentMaxData()
            ),
            updateCurrentValue = {},
            toEquipDetail = { },
            toCharacterRankEquip = { _, _ -> }
        )
    }
}

@CombinedPreviews
@Composable
private fun StarSelectContentPreview() {
    PreviewLayout {
        StarSelectContent(
            currentValue = CharacterProperty(rarity = 5),
            max = 6,
            updateCurrentValue = { }
        )
    }
}

@CombinedPreviews
@Composable
private fun SpineTypeSelectDialogPreview() {
    PreviewLayout {
        SpineTypeSelectDialog(
            openDialog = remember {
                mutableStateOf(true)
            },
            idList = arrayListOf(1, 2, 3)
        )
    }
}

