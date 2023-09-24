package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterHomePageComment
import cn.wthee.pcrtool.data.db.view.CharacterInfoPro
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.IconHorizontalPagerIndicator
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.copyText
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.fixedStr
import cn.wthee.pcrtool.viewmodel.CharacterViewModel

/**
 * 角色基本信息
 *
 * @param unitId 角色编号
 */
@Composable
fun CharacterBasicInfo(
    unitId: Int,
    viewModel: CharacterViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    //角色基本信息
    val basicInfoFlow = remember(unitId) {
        viewModel.getCharacter(unitId)
    }
    val basicInfo by basicInfoFlow.collectAsState(initial = null)
    //主页交流文本
    val homePageCommentListFlow = remember(unitId) {
        viewModel.getHomePageComments(unitId)
    }
    val homePageCommentList by homePageCommentListFlow.collectAsState(initial = arrayListOf())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        basicInfo?.let { info ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BasicInfo(info = info)
                HomePageCommentInfo(info.getSelf(), homePageCommentList)
                RoomComment(unitId = unitId, viewModel = viewModel)
            }
        }
    }

}

/**
 * 角色基本信息
 */
@Composable
private fun BasicInfo(info: CharacterInfoPro) {
    Column(
        modifier = Modifier
            .padding(start = Dimen.largePadding, end = Dimen.largePadding)
            .fillMaxWidth()
    ) {
        //标题
        MainText(
            text = info.catchCopy.deleteSpace,
            modifier = Modifier
                .padding(Dimen.largePadding)
                .fillMaxWidth(),
            selectable = true
        )
        //介绍
        Subtitle2(
            text = info.getIntroText(),
            selectable = true
        )
        //角色
        SingleRow(title = stringResource(id = R.string.character), content = info.unitName)
        //现实名字
        SingleRow(title = stringResource(id = R.string.name), content = info.actualName.fixedStr)
        //cv
        SingleRow(title = stringResource(id = R.string.cv), content = info.voice)

        //身高、体重
        TwoColumnsInfo(
            stringResource(id = R.string.title_height),
            "${info.height.fixedStr} CM",
            stringResource(id = R.string.title_weight),
            "${info.weight.fixedStr} KG"
        )
        //生日、年龄
        TwoColumnsInfo(
            stringResource(id = R.string.title_birth),
            stringResource(
                id = R.string.date_m_d,
                info.birthMonth.fixedStr,
                info.birthDay.fixedStr
            ),
            stringResource(id = R.string.age),
            info.age.fixedStr
        )
        //血型、种族
        TwoColumnsInfo(
            stringResource(id = R.string.title_blood),
            info.bloodType.fixedStr,
            stringResource(id = R.string.title_race),
            info.race,
        )
        //公会
        TwoRow(title = stringResource(id = R.string.title_guild), content = info.guild)
        //兴趣
        TwoRow(title = stringResource(id = R.string.title_fav), content = info.favorite)
    }
}

/**
 * 主页交流信息
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomePageCommentInfo(
    selfText: String?,
    homePageCommentList: List<CharacterHomePageComment>
) {
    //介绍信息
    selfText?.let {
        Row(modifier = Modifier.padding(start = Dimen.largePadding, top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_self),
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        CommentText(text = it)
    }

    //主页交流
    val pagerState = rememberPagerState { homePageCommentList.size }

    Row(
        modifier = Modifier.padding(
            start = Dimen.largePadding,
            top = Dimen.mediumPadding,
            bottom = Dimen.mediumPadding
        )
    ) {
        MainTitleText(
            text = stringResource(id = R.string.title_comments),
            modifier = Modifier.weight(0.15f)
        )
        MainTitleText(
            text = stringResource(id = R.string.title_home_page_comments),
            modifier = Modifier
                .padding(start = Dimen.smallPadding)
                .weight(0.15f)
        )
        Spacer(modifier = Modifier.weight(0.7f))
    }
    //多星级时
    if (homePageCommentList.isNotEmpty()) {
        val tabs = arrayListOf<String>()
        homePageCommentList.forEach {
            tabs.add(
                "★" + if (it.unitId % 100 / 10 == 0) {
                    "1"
                } else {
                    "${it.unitId % 100 / 10}"
                }
            )
        }
        MainTabRow(
            pagerState = pagerState,
            tabs = tabs,
            modifier = Modifier
                .padding(horizontal = Dimen.mediumPadding)
                .fillMaxWidth(homePageCommentList.size * 0.33f)
        )

        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { index ->
            Column {
                homePageCommentList[index].getCommentList().forEachIndexed { cIndex, s ->
                    CommentText(cIndex, s)
                }
            }
        }
    }


}

/**
 * 小屋交流文本
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RoomComment(unitId: Int, viewModel: CharacterViewModel) {
    val roomCommentsFlow = remember(unitId) {
        viewModel.getRoomComments(unitId)
    }
    val roomComments by roomCommentsFlow.collectAsState(initial = null)


    Row(
        modifier = Modifier.padding(
            start = Dimen.largePadding,
            top = Dimen.mediumPadding,
            bottom = Dimen.mediumPadding
        )
    ) {
        MainTitleText(
            text = stringResource(id = R.string.title_comments),
            modifier = Modifier.weight(0.15f)
        )
        MainTitleText(
            text = stringResource(id = R.string.title_room_comments),
            modifier = Modifier
                .padding(start = Dimen.smallPadding)
                .weight(0.15f)
        )
        Spacer(modifier = Modifier.weight(0.7f))
    }
    roomComments?.let { list ->
        val pagerState = rememberPagerState { list.size }

        //多角色时，显示角色图标
        if (list.size > 1) {
            val urls = arrayListOf<String>()
            list.forEach { roomComment ->
                urls.add(
                    ImageRequestHelper.getInstance().getMaxIconUrl(roomComment.unitId)
                )
            }
            IconHorizontalPagerIndicator(pagerState, urls)
        }
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top
        ) { index ->
            Column {
                list[index].getCommentList().forEachIndexed { cIndex, s ->
                    CommentText(cIndex, s)
                }
                CommonSpacer()
            }
        }
    }
}

/**
 * 单行
 */
@Composable
private fun SingleRow(title: String, content: String) {
    Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
        MainTitleText(
            text = title,
            modifier = Modifier.weight(0.15f)
        )
        MainContentText(
            text = content,
            modifier = Modifier.weight(0.85f),
            selectable = true
        )
    }
}

/**
 * 双行显示
 */
@Composable
private fun TwoRow(title: String, content: String) {
    Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
        MainTitleText(
            text = title,
            modifier = Modifier.weight(0.15f)
        )
        Spacer(modifier = Modifier.weight(0.85f))
    }
    MainContentText(
        text = content,
        modifier = Modifier.padding(Dimen.mediumPadding),
        textAlign = TextAlign.Start,
        selectable = true
    )
}

/**
 * 两列信息
 */
@Composable
private fun TwoColumnsInfo(
    title0: String,
    text0: String,
    title1: String,
    text1: String
) {
    Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
        MainTitleText(
            text = title0,
            modifier = Modifier.weight(0.15f)
        )
        MainContentText(
            text = text0,
            modifier = Modifier
                .weight(0.35f)
                .padding(end = Dimen.mediumPadding),
            selectable = true
        )
        MainTitleText(
            text = title1,
            modifier = Modifier.weight(0.15f)
        )
        MainContentText(
            text = text1,
            modifier = Modifier.weight(0.35f),
            selectable = true
        )
    }
}

/**
 * 文本
 */
@Composable
private fun CommentText(index: Int? = null, text: String) {
    val indexStr = if (index != null) "${index + 1}、" else ""
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .padding(
                horizontal = Dimen.largePadding,
                vertical = Dimen.smallPadding
            )
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                VibrateUtil(context).single()
                copyText(context, text)
            }
            .padding(Dimen.smallPadding),
    ) {
        MainContentText(text = indexStr)
        MainContentText(
            text = text,
            textAlign = TextAlign.Start,
            selectable = true
        )
    }
}

/**
 * 角色基本信息预览
 */
@CombinedPreviews
@Composable
private fun BasicInfoPreview() {
    PreviewLayout {
        BasicInfo(info = CharacterInfoPro())
    }
}