package cn.wthee.pcrtool.ui.character.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GuildData
import cn.wthee.pcrtool.data.enums.AtkType
import cn.wthee.pcrtool.data.enums.CharacterLimitType
import cn.wthee.pcrtool.data.enums.CharacterSortType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.PositionType
import cn.wthee.pcrtool.data.enums.TalentType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.navigation.navigateUpSheet
import cn.wthee.pcrtool.ui.components.ChipGroup
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainInputText
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorPink
import kotlinx.coroutines.launch


/**
 * 角色筛选
 */
@Composable
fun CharacterListFilterScreen(
    characterListFilterViewModel: CharacterListFilterViewModel = hiltViewModel()
) {
    val uiState by characterListFilterViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    MainScaffold(
        mainFabIcon = MainIconType.OK,
        onMainFabClick = {
            scope.launch {
                navigateUpSheet()
            }
        }
    ) {
        uiState.filter?.let {
            CharacterListFilterContent(
                filter = it,
                raceList = uiState.raceList,
                guildList = uiState.guildList,
                hasTalent = uiState.hasTalent,
                updateFilter = characterListFilterViewModel::updateFilter
            )
        }
    }
}

@Composable
private fun CharacterListFilterContent(
    filter: FilterCharacter,
    raceList: List<String>,
    guildList: List<GuildData>,
    hasTalent: Boolean,
    updateFilter: (FilterCharacter) -> Unit
) {
    val scope = rememberCoroutineScope()
    //名称
    val textState = remember { mutableStateOf(filter.name) }
    filter.name = textState.value

    //排序类型筛选
    val sortTypeIndex = remember {
        mutableIntStateOf(filter.sortType.type)
    }
    filter.sortType = CharacterSortType.getByType(sortTypeIndex.intValue)

    //排序方式筛选
    val sortAscIndex = remember {
        mutableIntStateOf(if (filter.asc) 0 else 1)
    }
    filter.asc = sortAscIndex.intValue == 0

    //收藏筛选
    val favoriteIndex = remember {
        mutableIntStateOf(if (filter.all) 0 else 1)
    }
    filter.all = favoriteIndex.intValue == 0

    //六星筛选
    val r6Index = remember {
        mutableIntStateOf(filter.r6)
    }
    filter.r6 = r6Index.intValue

    //位置筛选
    val positionIndex = remember {
        mutableIntStateOf(filter.position)
    }
    filter.position = positionIndex.intValue

    //攻击类型
    val atkIndex = remember {
        mutableIntStateOf(filter.atk)
    }
    filter.atk = atkIndex.intValue

    //天赋类型
    val talentIndex = remember {
        mutableIntStateOf(filter.talentType)
    }
    filter.talentType = talentIndex.intValue

    //公会
    val guildIndex = remember {
        mutableIntStateOf(filter.guild)
    }
    filter.guild = guildIndex.intValue

    //种族
    val raceIndex = remember {
        mutableIntStateOf(filter.race)
    }
    filter.race = raceIndex.intValue

    //限定类型
    val typeIndex = remember {
        mutableIntStateOf(filter.type)
    }
    filter.type = typeIndex.intValue

    //专用装备
    val uniqueEquipTypeIndex = remember {
        mutableIntStateOf(filter.uniqueEquipType)
    }
    filter.uniqueEquipType = uniqueEquipTypeIndex.intValue


    //更新信息
    LaunchedEffect(
        textState.value, sortTypeIndex.intValue, sortAscIndex.intValue, favoriteIndex.intValue,
        r6Index.intValue, positionIndex.intValue, atkIndex.intValue, guildIndex.intValue,
        raceIndex.intValue, typeIndex.intValue, uniqueEquipTypeIndex.intValue, talentIndex.intValue
    ) {
        updateFilter(filter)
    }


    //选择状态
    Column(
        modifier = Modifier
            .padding(start = Dimen.largePadding, end = Dimen.largePadding)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        //角色名搜索
        MainInputText(
            textState = textState,
            leadingIcon = MainIconType.CHARACTER,
            onDone = {
                scope.launch {
                    navigateUpSheet()
                }
            },
            label = stringResource(id = R.string.character_name)
        )
        //排序类型
        MainText(
            text = stringResource(id = R.string.title_sort),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val sortChipData = arrayListOf(
            ChipData(stringResource(id = R.string.sort_date)),
            ChipData(stringResource(id = R.string.age)),
            ChipData(stringResource(id = R.string.title_height)),
            ChipData(stringResource(id = R.string.title_weight)),
            ChipData(stringResource(id = R.string.title_position)),
            ChipData(stringResource(id = R.string.title_birth)),
            ChipData(stringResource(id = R.string.title_unlock_6))
        )
        ChipGroup(
            items = sortChipData,
            selectIndex = sortTypeIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //排序方式
        MainText(
            text = stringResource(id = R.string.sort_asc_desc),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val sortAscChipData = arrayListOf(
            ChipData(stringResource(id = R.string.sort_asc)),
            ChipData(stringResource(id = R.string.sort_desc)),
        )
        ChipGroup(
            items = sortAscChipData,
            selectIndex = sortAscIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //收藏
        MainText(
            text = stringResource(id = R.string.title_favorite),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val favoriteChipData = arrayListOf(
            ChipData(stringResource(id = R.string.all)),
            ChipData(stringResource(id = R.string.favorite)),
        )
        ChipGroup(
            items = favoriteChipData,
            selectIndex = favoriteIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //类型
        MainText(
            text = stringResource(id = R.string.title_type),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val typeChipData = arrayListOf(
            ChipData(text = stringResource(id = R.string.all)),
            ChipData(
                text = stringResource(id = CharacterLimitType.NORMAL.typeNameId),
                color = CharacterLimitType.NORMAL.color
            ),
            ChipData(
                text = stringResource(id = CharacterLimitType.LIMIT.typeNameId),
                color = CharacterLimitType.LIMIT.color
            ),
            ChipData(
                text = stringResource(id = CharacterLimitType.EVENT.typeNameId),
                color = CharacterLimitType.EVENT.color
            ),
            ChipData(
                text = stringResource(id = CharacterLimitType.EXTRA.typeNameId),
                color = CharacterLimitType.EXTRA.color
            ),
        )
        ChipGroup(
            items = typeChipData,
            selectIndex = typeIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )

        //六星
        val r6ChipData = arrayListOf(
            ChipData(stringResource(id = R.string.all)),
            ChipData(text = stringResource(id = R.string.six_star), color = colorPink),
            ChipData(stringResource(id = R.string.six_locked)),
        )
        //是否选择了六星解放排序
        val isUnlock6SortType = sortTypeIndex.intValue == CharacterSortType.SORT_UNLOCK_6.type
        //未选择六星解放排序时，显示星级筛选
        ExpandAnimation(visible = !isUnlock6SortType) {
            Column {
                MainText(
                    text = stringResource(id = R.string.title_rarity),
                    modifier = Modifier.padding(top = Dimen.largePadding)
                )
                ChipGroup(
                    items = r6ChipData,
                    selectIndex = r6Index,
                    modifier = Modifier.padding(Dimen.smallPadding)
                )
            }
        }

        //位置
        MainText(
            text = stringResource(id = R.string.title_position),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val positionChipData = arrayListOf(
            ChipData(stringResource(id = R.string.all)),
            ChipData(
                text = stringResource(id = PositionType.POSITION_FRONT.typeNameId),
                color = PositionType.POSITION_FRONT.color
            ),
            ChipData(
                text = stringResource(id = PositionType.POSITION_MIDDLE.typeNameId),
                color = PositionType.POSITION_MIDDLE.color
            ),
            ChipData(
                text = stringResource(id = PositionType.POSITION_BACK.typeNameId),
                color = PositionType.POSITION_BACK.color
            ),
        )
        ChipGroup(
            items = positionChipData,
            selectIndex = positionIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //天赋类型
        if (hasTalent) {
            MainText(
                text = stringResource(id = R.string.talent_type),
                modifier = Modifier.padding(top = Dimen.largePadding)
            )
            val talentChipData = arrayListOf(
                ChipData(stringResource(id = R.string.all))
            )
            TalentType.entries.forEachIndexed { index, talentType ->
                if (index != 0) {
                    talentChipData.add(
                        ChipData(
                            text = stringResource(id = talentType.typeNameId),
                            color = talentType.color
                        )
                    )
                }
            }
            ChipGroup(
                items = talentChipData,
                selectIndex = talentIndex,
                modifier = Modifier.padding(Dimen.smallPadding)
            )
        }

        //攻击类型
        MainText(
            text = stringResource(id = R.string.atk_type),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val atkChipData = arrayListOf(
            ChipData(stringResource(id = R.string.all)),
            ChipData(
                text = stringResource(id = AtkType.PHYSICAL.typeNameId),
                color = AtkType.PHYSICAL.color
            ),
            ChipData(
                text = stringResource(id = AtkType.MAGIC.typeNameId),
                color = AtkType.MAGIC.color
            ),
        )
        ChipGroup(
            items = atkChipData,
            selectIndex = atkIndex,
            modifier = Modifier.padding(Dimen.smallPadding)
        )
        //专用装备类型
        MainText(
            text = stringResource(id = R.string.tool_unique_equip),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val uniqueEquipTypeChipData = arrayListOf(
            ChipData(stringResource(id = R.string.all)),
            ChipData(
                text = stringResource(id = R.string.tool_unique_equip) + 1
            ),
            ChipData(
                text = stringResource(id = R.string.tool_unique_equip) + 2
            ),
            ChipData(
                text = stringResource(id = R.string.other)
            ),
        )
        ChipGroup(
            items = uniqueEquipTypeChipData,
            selectIndex = uniqueEquipTypeIndex,
            modifier = Modifier.padding(Dimen.smallPadding)
        )
        //种族
        if (raceList.isNotEmpty()) {
            MainText(
                text = stringResource(id = R.string.title_race),
                modifier = Modifier.padding(top = Dimen.largePadding)
            )
            val raceChipData = arrayListOf(
                ChipData(stringResource(id = R.string.all)),
                ChipData(stringResource(id = R.string.title_race_multiple)),
            )
            raceList.forEach { raceData ->
                raceChipData.add(ChipData(raceData))
            }
            ChipGroup(
                items = raceChipData,
                selectIndex = raceIndex,
                modifier = Modifier.padding(Dimen.smallPadding),
            )
        }
        //公会名
        if (guildList.isNotEmpty()) {
            MainText(
                text = stringResource(id = R.string.title_guild),
                modifier = Modifier.padding(top = Dimen.largePadding)
            )
            val guildChipData = arrayListOf(
                ChipData(stringResource(id = R.string.all)),
                ChipData(stringResource(id = R.string.no_guild)),
            )
            guildList.forEach { guildData ->
                guildChipData.add(ChipData(guildData.guildName))
            }
            ChipGroup(
                items = guildChipData,
                selectIndex = guildIndex,
                modifier = Modifier.padding(Dimen.smallPadding),
            )
            CommonSpacer()
        }
    }
}


@CombinedPreviews
@Composable
private fun CharacterListFilterContentPreview() {
    PreviewLayout {
        CharacterListFilterContent(
            filter = FilterCharacter(),
            updateFilter = {},
            guildList = emptyList(),
            raceList = emptyList(),
            hasTalent = true
        )
    }
}
