package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpFavoriteData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.PvpResultData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.SubButton
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.components.placeholder
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.fillZero
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.intArrayList
import kotlinx.coroutines.launch
import kotlin.math.round


/**
 * 查询结果页面
 */
@Composable
fun PvpSearchResult(
    result: ResponseData<List<PvpResultData>>?,
    favoritesList: List<PvpFavoriteData>,
    resultListState: LazyGridState,
    floatWindow: Boolean,
    research: () -> Unit,
    delete: (String, String) -> Unit,
    insert: (PvpFavoriteData) -> Unit,
) {
    val placeholder = result == null

    //收藏信息
    val favoritesAtkList = arrayListOf<String>()
    favoritesList.forEach { data ->
        favoritesAtkList.add(data.atks)
    }

    val context = LocalContext.current
    val vibrated = remember {
        mutableStateOf(false)
    }
    //宽度
    val itemWidth = getItemWidth(floatWindow)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (!placeholder) {
            if (result!!.status == 0) {
                //振动提醒
                if (!vibrated.value) {
                    vibrated.value = true
                    VibrateUtil(context).done()
                }
                val hasData = result.data!!.isNotEmpty()
                if (hasData) {
                    //查询成功
                    val list = result.data!!.sortedByDescending { it.up }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        //展示查询结果
                        LazyVerticalGrid(
                            state = resultListState,
                            columns = GridCells.Adaptive(itemWidth)
                        ) {
                            itemsIndexed(
                                items = list,
                                key = { _, it ->
                                    it.id
                                }
                            ) { index, item ->
                                PvpResultItem(
                                    liked = favoritesAtkList.contains(item.atk),
                                    index = index + 1,
                                    pvpResultData = item,
                                    floatWindow = floatWindow,
                                    delete = delete,
                                    insert = insert
                                )
                            }
                            item {
                                CommonSpacer()
                            }
                        }
                    }
                } else {
                    CenterTipText(
                        text = stringResource(id = R.string.pvp_no_data)
                    ) {
                        // 无查询结果，提示去网站查询
                        val searchUrl = stringResource(id = R.string.pcrdfans_search_url)
                        SubButton(
                            text = stringResource(id = R.string.pvp_search_on_web),
                            modifier = Modifier.padding(top = Dimen.mediumPadding),
                            onClick = {
                                BrowserUtil.open(searchUrl)
                            }
                        )
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = Dimen.largePadding)
                ) {
                    CenterTipText(
                        text = stringResource(id = R.string.data_get_error)
                    ) {
                        SubButton(
                            text = stringResource(id = R.string.research),
                            modifier = Modifier.padding(top = Dimen.mediumPadding),
                            onClick = {
                                research()
                            }
                        )
                    }
                }

            }
        } else {
            //占位图
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                //展示查询结果
                LazyVerticalGrid(
                    state = resultListState,
                    columns = GridCells.Adaptive(itemWidth)
                ) {
                    items(10) {
                        PvpResultItem(
                            liked = false,
                            index = 0,
                            pvpResultData = PvpResultData(),
                            floatWindow = floatWindow,
                            delete = delete,
                            insert = insert
                        )
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }

    }

}

/**
 * 查询结果 Item
 *
 * @param index 序号
 */
@Composable
private fun PvpResultItem(
    liked: Boolean,
    index: Int,
    pvpResultData: PvpResultData,
    floatWindow: Boolean,
    delete: (String, String) -> Unit,
    insert: (PvpFavoriteData) -> Unit,
) {
    val placeholder = pvpResultData.id == ""
    val scope = rememberCoroutineScope()

    val largePadding = if (floatWindow) Dimen.mediumPadding else Dimen.largePadding
    val mediumPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediumPadding

    Column(
        modifier = Modifier.padding(
            horizontal = largePadding,
            vertical = mediumPadding
        )
    ) {
        Row(
            modifier = Modifier.padding(bottom = mediumPadding),
            verticalAlignment = Alignment.Bottom
        ) {
            MainTitleText(
                text = stringResource(id = R.string.team_no, index.toString().fillZero()),
                modifier = Modifier.placeholder(visible = placeholder)
            )
            Spacer(modifier = Modifier.weight(1f))
            //收藏
            if (!placeholder) {
                MainIcon(
                    data = if (liked) {
                        MainIconType.FAVORITE_FILL
                    } else {
                        MainIconType.FAVORITE_LINE
                    },
                    size = Dimen.fabIconSize,
                    onClick = {
                        scope.launch {
                            if (liked) {
                                //已收藏，取消收藏
                                delete(pvpResultData.atk, pvpResultData.def)
                            } else {
                                //未收藏，添加收藏
                                insert(
                                    PvpFavoriteData(
                                        id = pvpResultData.id,
                                        atks = pvpResultData.atk,
                                        defs = pvpResultData.def,
                                        atkTalentIds = pvpResultData.atkTalents,
                                        defTalentIds = pvpResultData.defTalents,
                                        atkPositions = pvpResultData.atkPositions,
                                        defPositions = pvpResultData.defPositions,
                                        date = getToday(true),
                                        region = MainActivity.regionType.value
                                    )
                                )
                            }
                        }
                    }
                )
            }
        }

        MainCard(
            modifier = Modifier.placeholder(
                visible = placeholder,
                shape = MaterialTheme.shapes.medium
            )
        ) {
            val upRatio = if (pvpResultData.up == 0) 0 else {
                round(pvpResultData.up * 1.0 / (pvpResultData.up + pvpResultData.down) * 100).toInt()
            }
            //点赞信息
            Row(
                modifier = Modifier.padding(horizontal = mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainContentText(
                    text = "${upRatio}%",
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(0.3f)
                )
                MainContentText(
                    text = pvpResultData.up.toString(),
                    color = colorGreen,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(0.3f)
                )
                MainContentText(
                    text = pvpResultData.down.toString(),
                    color = colorRed,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(if (floatWindow) 0.3f else 1f)
                )
            }
            //队伍角色图标
            //进攻
            PvpUnitIconLine(
                modifier = Modifier.padding(bottom = mediumPadding),
                ids = if (placeholder) arrayListOf(-1) else pvpResultData.atk.intArrayList,
                talentIdList = pvpResultData.atkTalents.intArrayList,
                positionList = pvpResultData.atkPositions.intArrayList,
                floatWindow = floatWindow
            ) { }
        }

    }
}


@CombinedPreviews
@Composable
private fun PvpResultItemPreview() {
    val data = PvpResultData(
        "id",
        "1-2-3-4-5",
        "1-2-3-4-5",
        2,
        1000,
        200
    )
    PreviewLayout {
        PvpResultItem(
            liked = false,
            index = 0,
            pvpResultData = data,
            floatWindow = false,
            delete = { _, _ -> },
            insert = {}
        )
        PvpResultItem(
            liked = true,
            index = 0,
            pvpResultData = data,
            floatWindow = true,
            delete = { _, _ -> },
            insert = {}
        )
    }
}