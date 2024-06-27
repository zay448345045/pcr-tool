package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.CharacterProfileInfo
import cn.wthee.pcrtool.data.enums.CharacterListShowType
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.isFilter
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.character.profile.CharacterProfileCommonContent
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CharacterTagRow
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.ExpandableFab
import cn.wthee.pcrtool.ui.components.IconListContent
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.LifecycleEffect
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainImage
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.RATIO
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.components.placeholder
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.ui.theme.TrapezoidShape
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.fixedStr
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.toDate
import coil3.BitmapImage
import coil3.annotation.ExperimentalCoilApi
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 角色列表
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CharacterListScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    toCharacterDetail: (Int) -> Unit,
    toFilterCharacter: (String) -> Unit,
    characterListViewModel: CharacterListViewModel = hiltViewModel(),
) {
    val uiState by characterListViewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyGridState()

    //初始筛选信息
    LifecycleEffect(Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_CREATE) {
        characterListViewModel.initFilter()
    }


    MainScaffold(
        fab = {
            CharacterListFabContent(
                count = uiState.characterList?.size ?: 0,
                scrollState = scrollState,
                filter = uiState.filter,
                resetFilter = characterListViewModel::resetFilter,
                changeShowType = characterListViewModel::changeShowType,
                toFilterCharacter = toFilterCharacter
            )
        },
        secondLineFab = {
            //已收藏
            if (uiState.favoriteIdList.isNotEmpty()) {
                ExpandableFab(
                    expanded = uiState.openDialog,
                    onClick = {
                        characterListViewModel.changeDialog(true)
                    },
                    icon = MainIconType.FAVORITE_FILL,
                    text = uiState.favoriteIdList.size.toString(),
                    isSecondLineFab = true
                ) {
                    IconListContent(
                        idList = uiState.favoriteIdList,
                        title = stringResource(id = R.string.favorite),
                        iconResourceType = IconResourceType.CHARACTER,
                        onClickItem = {
                            characterListViewModel.changeDialog(false)
                            toCharacterDetail(it)
                        }
                    )
                }
            }
        },
        enableClickClose = uiState.openDialog,
        onCloseClick = {
            characterListViewModel.changeDialog(false)
        },
        mainFabIcon = if (uiState.openDialog) MainIconType.CLOSE else MainIconType.BACK,
        onMainFabClick = {
            if (uiState.openDialog) {
                characterListViewModel.changeDialog(false)
            } else {
                navigateUp()
            }
        }
    ) {
        StateBox(
            stateType = uiState.loadState,
        ) {
            CharacterListContent(
                animatedVisibilityScope = animatedVisibilityScope,
                characterList = uiState.characterList,
                scrollState = scrollState,
                favoriteIdList = uiState.favoriteIdList,
                showType = uiState.showType,
                toCharacterDetail = toCharacterDetail
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.CharacterListContent(
    animatedVisibilityScope: AnimatedVisibilityScope,
    characterList: List<CharacterInfo>?,
    scrollState: LazyGridState,
    favoriteIdList: List<Int>,
    showType: CharacterListShowType,
    toCharacterDetail: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(
            when (showType) {
                CharacterListShowType.CARD, CharacterListShowType.ICON_TAG -> getItemWidth()
                CharacterListShowType.ICON -> Dimen.iconSize + Dimen.largePadding * 2
            }
        ),
        state = scrollState
    ) {
        characterList?.let {
            items(
                items = characterList,
                key = {
                    it.id
                }
            ) {
                when (showType) {
                    CharacterListShowType.CARD -> CharacterItemContent(
                        unitId = it.id,
                        animatedVisibilityScope = animatedVisibilityScope,
                        characterInfo = it,
                        favorite = favoriteIdList.contains(it.id),
                        modifier = Modifier.padding(Dimen.mediumPadding),
                        onClick = {
                            toCharacterDetail(it.id)
                        }
                    )

                    CharacterListShowType.ICON_TAG -> CharacterIconAndTextContent(
                        unitId = it.id,
                        animatedVisibilityScope = animatedVisibilityScope,
                        character = it,
                        favorite = favoriteIdList.contains(it.id),
                        onClick = {
                            toCharacterDetail(it.id)
                        }
                    )

                    CharacterListShowType.ICON -> Box(
                        modifier = Modifier.padding(Dimen.mediumPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        MainIcon(
                            data = ImageRequestHelper.getInstance().getMaxIconUrl(it.id),
                            onClick = {
                                toCharacterDetail(it.id)
                            }
                        )
                    }
                }

            }
        }

        items(
            when (showType) {
                CharacterListShowType.CARD -> 2
                CharacterListShowType.ICON_TAG -> 3
                CharacterListShowType.ICON -> 5
            }
        ) {
            CommonSpacer()
        }
    }
}

@Composable
private fun CharacterListFabContent(
    count: Int,
    scrollState: LazyGridState,
    filter: FilterCharacter?,
    resetFilter: () -> Unit,
    changeShowType: () -> Unit,
    toFilterCharacter: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    //回到顶部
    MainSmallFab(
        iconType = MainIconType.TOP,
        onClick = {
            coroutineScope.launch {
                scrollState.scrollToItem(0)
            }
        }
    )
    //重置筛选
    if (filter?.isFilter() == true) {
        MainSmallFab(
            iconType = MainIconType.RESET,
            onClick = {
                resetFilter()
            }
        )
    }
    //展示类型
    MainSmallFab(
        iconType = MainIconType.VIEW_MODULE,
        onClick = {
            changeShowType()
        }
    )
    // 数量显示&筛选按钮
    MainSmallFab(
        iconType = MainIconType.CHARACTER,
        text = "$count",
        onClick = {
            filter?.let {
                toFilterCharacter(Json.encodeToString(filter))
            }
        }
    )

}

/**
 * 角色列表项
 */
@OptIn(ExperimentalCoilApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CharacterItemContent(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit,
    unitId: Int,
    characterInfo: CharacterInfo?,
    favorite: Boolean
) {

    //图片是否加载成功
    var imageLoadSuccess by remember {
        mutableStateOf(false)
    }
    //预览时，默认为 true
    val defaultError = LocalInspectionMode.current
    var imageLoadError by remember {
        mutableStateOf(defaultError)
    }
    //主色
    val initColor = colorWhite
    var cardMaskColor by remember {
        mutableStateOf(initColor)
    }
    //主要字体颜色
    val textColor = if (imageLoadSuccess) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.onSurface
    }


    MainCard(
        modifier = modifier
            .placeholder(characterInfo?.id == -1)
            .then(
                if (MainActivity.animOnFlag) {
                    Modifier.sharedElement(
                        state = rememberSharedContentState(
                            key = "CharacterItemContent-$unitId"
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                } else {
                    Modifier
                }
            ),
        onClick = onClick
    ) {
        Box(modifier = Modifier.height(IntrinsicSize.Min)) {
            //图片
            MainImage(
                data = ImageRequestHelper.getInstance().getMaxCardUrl(unitId),
                ratio = RATIO,
                contentScale = ContentScale.FillHeight,
                onError = { imageLoadError = true }
            ) { result ->
                imageLoadSuccess = true
                //取色
                val bitmap = ((result.image) as BitmapImage).bitmap
                Palette.from(bitmap).generate { palette ->
                    palette?.let {
                        cardMaskColor = Color(it.getDominantColor(Color.Transparent.toArgb()))
                    }
                }
            }
            if (characterInfo != null) {
                //名称阴影效果
                if (imageLoadSuccess) {
                    CharacterName(
                        color = MaterialTheme.colorScheme.primary,
                        name = characterInfo.getNameF(),
                        nameExtra = characterInfo.getNameL(),
                        isBorder = true,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }
                //名称
                CharacterName(
                    color = textColor,
                    name = characterInfo.getNameF(),
                    nameExtra = characterInfo.getNameL(),
                    isBorder = false,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            } else {
                //暂未登场
                if (imageLoadSuccess) {
                    CharacterName(
                        color = MaterialTheme.colorScheme.primary,
                        name = stringResource(id = R.string.unknown_character),
                        nameExtra = "",
                        isBorder = true,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }
                //名称
                CharacterName(
                    color = textColor,
                    name = stringResource(id = R.string.unknown_character),
                    nameExtra = "",
                    isBorder = false,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }


            //其它信息
            FadeAnimation(
                visible = imageLoadSuccess || imageLoadError || LocalInspectionMode.current,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                //年龄等
                if (characterInfo != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(1 - RATIO_GOLDEN)
                            .fillMaxHeight()
                            .clip(TrapezoidShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        cardMaskColor,
                                        cardMaskColor,
                                        MaterialTheme.colorScheme.primary,
                                    )
                                ),
                                alpha = 0.6f
                            ),
                        horizontalAlignment = Alignment.End,
                    ) {
                        if (BuildConfig.DEBUG) {
                            CaptionText(text = characterInfo.id.toString())
                        }

                        Column(
                            modifier = Modifier
                                .padding(
                                    horizontal = Dimen.mediumPadding,
                                    vertical = Dimen.smallPadding
                                ),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            //年龄
                            Subtitle2(
                                text = characterInfo.age.fixedStr,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            //体重
                            Subtitle2(
                                text = "${characterInfo.weight.fixedStr} KG",
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            //身高
                            Subtitle2(
                                text = "${characterInfo.height.fixedStr} CM",
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            //生日
                            Subtitle2(
                                text = stringResource(
                                    id = R.string.date_m_d,
                                    characterInfo.birthMonth.fixedStr,
                                    characterInfo.birthDay.fixedStr
                                ),
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }

                        //获取方式等
                        Box(
                            modifier = Modifier
                                .padding(end = Dimen.smallPadding)
                                .weight(1f),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            CharacterTagRow(
                                characterInfo = characterInfo,
                                horizontalArrangement = Arrangement.End
                            )
                        }

                        //最近登场日期
                        CaptionText(
                            text = characterInfo.startTime.formatTime.toDate,
                            color = textColor,
                            modifier = Modifier.padding(
                                end = Dimen.mediumPadding,
                                top = Dimen.mediumPadding,
                                bottom = Dimen.smallPadding
                            )
                        )
                    }
                }
            }

            //收藏标识
            FadeAnimation(visible = favorite && (imageLoadSuccess || imageLoadError)) {
                MainIcon(
                    data = MainIconType.FAVORITE_FILL,
                    size = Dimen.textIconSize,
                    modifier = Modifier.padding(Dimen.mediumPadding)
                )
            }
        }
    }
}

/**
 * 角色列表项（图标及基本信息）
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.CharacterIconAndTextContent(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: () -> Unit,
    unitId: Int,
    character: CharacterInfo?,
    favorite: Boolean
) {
    var expand by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .padding(
                top = Dimen.largePadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding,
            )
            .then(
                if (MainActivity.animOnFlag) {
                    Modifier.sharedElement(
                        state = rememberSharedContentState(
                            key = "CharacterItemContent-$unitId"
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                } else {
                    Modifier
                }
            )
    ) {
        //图标
        MainIcon(
            modifier = Modifier,
            data = ImageRequestHelper.getInstance().getMaxIconUrl(unitId),
            onClick = onClick
        )

        if (character != null) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    //名称
                    MainTitleText(
                        text = character.getNameF(),
                        modifier = Modifier.padding(start = Dimen.mediumPadding),
                        selectable = true,
                        maxLines = 1
                    )
                    //限定类型名称
                    MainTitleText(
                        text = character.getNameL(),
                        modifier = Modifier.padding(start = Dimen.mediumPadding),
                        selectable = true,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    //收藏
                    if (favorite) {
                        MainIcon(
                            data = MainIconType.FAVORITE_FILL,
                            size = Dimen.textIconSize,
                            modifier = Modifier.padding(end = Dimen.mediumPadding)
                        )
                    }
                }

                //基本信息
                MainCard(
                    modifier = Modifier
                        .padding(
                            start = Dimen.mediumPadding,
                            top = Dimen.mediumPadding,
                            bottom = Dimen.mediumPadding
                        ),
                    onClick = onClick
                ) {
                    //标签（获取方式等）
                    CharacterTagRow(
                        modifier = Modifier.padding(Dimen.mediumPadding),
                        characterInfo = character,
                        horizontalArrangement = Arrangement.End
                    )

                    Row(verticalAlignment = Alignment.Bottom) {
                        //最近登场日期
                        CaptionText(
                            text = character.startTime.formatTime.toDate,
                            modifier = Modifier
                                .padding(Dimen.mediumPadding)
                                .weight(1f),
                            textAlign = TextAlign.Start
                        )

                        //展开其他信息
                        IconTextButton(
                            modifier = Modifier.padding(end = Dimen.mediumPadding),
                            text = stringResource(id = R.string.character_basic_info),
                            icon = if (expand) MainIconType.UP else MainIconType.DOWN,
                            onClick = {
                                expand = !expand
                            }
                        )
                    }

                    //年龄等信息
                    val profile = CharacterProfileInfo(
                        unitId = character.id,
                        unitName = character.name,
                        voice = character.voice,
                        height = character.height,
                        weight = character.weight,
                        age = character.age,
                        birthMonth = character.birthMonth,
                        birthDay = character.birthDay,
                        race = character.race,
                        bloodType = character.bloodType,
                        guild = character.guild,
                        favorite = character.favorite,
                    )
                    if (expand) {
                        Column(
                            modifier = Modifier.padding(Dimen.mediumPadding),
                        ) {
                            CharacterProfileCommonContent(characterProfileInfo = profile)
                        }
                    }

                }
            }
        }
    }
}

/**
 * 角色名称
 */
@Composable
private fun CharacterName(
    color: Color,
    name: String,
    nameExtra: String,
    isBorder: Boolean,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .then(
                if (isBorder) {
                    Modifier
                        .padding(
                            start = Dimen.mediumPadding + Dimen.textElevation,
                            end = Dimen.mediumPadding,
                            top = Dimen.mediumPadding + Dimen.textElevation,
                            bottom = Dimen.mediumPadding
                        )

                } else {
                    Modifier
                        .padding(Dimen.mediumPadding)
                }
            )
            .fillMaxWidth(RATIO_GOLDEN)
    ) {
        Subtitle1(
            text = nameExtra,
            color = color,
            selectable = !isBorder
        )
        MainText(
            text = name,
            color = color,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge,
            selectable = !isBorder
        )
    }
}


@CombinedPreviews
@Composable
fun CharacterItemPreview() {
    PreviewLayout {
//        CharacterItemContent(
//            unitId = 100101,
//            character = CharacterInfo(
//                id = 100101,
//                position = 100,
//                name = stringResource(id = R.string.debug_name),
//                startTime = "2022-02-03 22:22:22",
//                uniqueEquipType = 2
//            ),
//            favorite = true,
//            onClick = {}
//        )
    }
}

