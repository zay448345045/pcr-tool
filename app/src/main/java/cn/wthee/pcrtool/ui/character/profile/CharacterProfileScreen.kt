package cn.wthee.pcrtool.ui.character.profile

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterHomePageComment
import cn.wthee.pcrtool.data.db.view.CharacterProfileInfo
import cn.wthee.pcrtool.data.db.view.RoomCommentData
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.IconHorizontalPagerIndicator
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.TabData
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.copyText
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.fixedStr

private const val TITLE_WEIGHT = 0.18f
private const val CONTENT_WEIGHT = 1 - TITLE_WEIGHT
/**
 * 角色基本信息
 */
@Composable
fun CharacterBasicInfo(
    characterProfileViewModel: CharacterProfileViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by characterProfileViewModel.uiState.collectAsStateWithLifecycle()


    MainScaffold {
        StateBox(stateType = uiState.loadState) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                uiState.profile?.let {
                    ProfileInfoContent(characterProfileInfo = it)
                    HomePageCommentContent(it.getSelf(), uiState.homePageCommentList)
                }
                RoomCommentContent(uiState.roomCommentList)
            }
        }
    }
}

/**
 * 角色基本信息
 */
@Composable
private fun ProfileInfoContent(characterProfileInfo: CharacterProfileInfo) {
    Column(
        modifier = Modifier
            .padding(start = Dimen.largePadding, end = Dimen.largePadding)
            .fillMaxWidth()
    ) {
        //标题
        MainText(
            text = characterProfileInfo.catchCopy.deleteSpace,
            modifier = Modifier
                .padding(Dimen.largePadding)
                .fillMaxWidth(),
            selectable = true
        )
        //介绍
        Subtitle2(
            text = characterProfileInfo.getIntroText(),
            selectable = true
        )
        CharacterProfileCommonContent(characterProfileInfo)
    }
}

/**
 * 角色资料通用内容
 */
@Composable
fun CharacterProfileCommonContent(characterProfileInfo: CharacterProfileInfo) {
    //角色
    SingleRow(
        title = stringResource(id = R.string.character),
        content = characterProfileInfo.unitName
    )

    if (characterProfileInfo.actualName != "") {
        //现实名字
        SingleRow(
            title = stringResource(id = R.string.name),
            content = characterProfileInfo.actualName.fixedStr
        )
    }

    //编号
    SingleRow(
        title = stringResource(id = R.string.id),
        content = characterProfileInfo.unitId.toString()
    )
    //cv
    SingleRow(title = stringResource(id = R.string.cv), content = characterProfileInfo.voice)

    //身高、体重
    TwoColumn(
        title0 = stringResource(id = R.string.title_height),
        text0 = "${characterProfileInfo.height.fixedStr} CM",
        title1 = stringResource(id = R.string.title_weight),
        text1 = "${characterProfileInfo.weight.fixedStr} KG"
    )
    //生日、年龄
    TwoColumn(
        title0 = stringResource(id = R.string.title_birth),
        text0 = stringResource(
            id = R.string.date_m_d,
            characterProfileInfo.birthMonth.fixedStr,
            characterProfileInfo.birthDay.fixedStr
        ),
        title1 = stringResource(id = R.string.age),
        text1 = characterProfileInfo.age.fixedStr
    )
    //血型、种族
    TwoColumn(
        title0 = stringResource(id = R.string.title_blood),
        text0 = characterProfileInfo.bloodType.fixedStr,
        title1 = stringResource(id = R.string.title_race),
        text1 = characterProfileInfo.race,
    )
    //公会
    TwoRow(title = stringResource(id = R.string.title_guild), content = characterProfileInfo.guild)
    //兴趣
    TwoRow(title = stringResource(id = R.string.title_fav), content = characterProfileInfo.favorite)
}

/**
 * 主页交流信息
 */
@Composable
private fun HomePageCommentContent(
    selfText: String?,
    homePageCommentList: List<CharacterHomePageComment>
) {
    //介绍信息
    selfText?.let {
        Row(modifier = Modifier.padding(start = Dimen.largePadding, top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_self),
                modifier = Modifier.weight(TITLE_WEIGHT)
            )
            Spacer(modifier = Modifier.weight(CONTENT_WEIGHT))
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
            modifier = Modifier.weight(TITLE_WEIGHT)
        )
        MainTitleText(
            text = stringResource(id = R.string.title_home_page_comments),
            modifier = Modifier
                .padding(start = Dimen.smallPadding)
                .weight(TITLE_WEIGHT)
        )
        Spacer(modifier = Modifier.weight(1 - TITLE_WEIGHT * 2))
    }
    //多星级时
    if (homePageCommentList.isNotEmpty()) {
        val tabs = arrayListOf<TabData>()
        homePageCommentList.forEach {
            tabs.add(
                TabData(
                    tab = stringResource(
                        id = R.string.star, if (it.unitId % 100 / 10 == 0) {
                            1
                        } else {
                            it.unitId % 100 / 10
                        }
                    )
                )
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
@Composable
private fun RoomCommentContent(roomCommentList: List<RoomCommentData>) {
    Row(
        modifier = Modifier.padding(
            start = Dimen.largePadding,
            top = Dimen.mediumPadding,
            bottom = Dimen.mediumPadding
        )
    ) {
        MainTitleText(
            text = stringResource(id = R.string.title_comments),
            modifier = Modifier.weight(TITLE_WEIGHT)
        )
        MainTitleText(
            text = stringResource(id = R.string.title_room_comments),
            modifier = Modifier
                .padding(start = Dimen.smallPadding)
                .weight(TITLE_WEIGHT)
        )
        Spacer(modifier = Modifier.weight(1 - TITLE_WEIGHT * 2))
    }
    roomCommentList.let { list ->
        val pagerState = rememberPagerState { list.size }

        //多角色时，显示角色图标
        if (list.size > 1) {
            val urlList = arrayListOf<String?>()
            list.forEach { roomComment ->
                urlList.add(
                    ImageRequestHelper.getInstance().getMaxIconUrl(roomComment.unitId)
                )
            }
            IconHorizontalPagerIndicator(pagerState, urlList)
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
            modifier = Modifier.weight(TITLE_WEIGHT)
        )
        MainContentText(
            text = content,
            modifier = Modifier.weight(CONTENT_WEIGHT),
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
            modifier = Modifier.weight(TITLE_WEIGHT)
        )
        Spacer(modifier = Modifier.weight(CONTENT_WEIGHT))
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
fun TwoColumn(
    title0: String,
    text0: String,
    title1: String,
    text1: String
) {
    Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
        MainTitleText(
            text = title0,
            modifier = Modifier.weight(TITLE_WEIGHT)
        )
        MainContentText(
            text = text0,
            modifier = Modifier
                .weight(0.5f - TITLE_WEIGHT)
                .padding(end = Dimen.mediumPadding),
            selectable = true
        )
        MainTitleText(
            text = title1,
            modifier = Modifier.weight(TITLE_WEIGHT)
        )
        MainContentText(
            text = text1,
            modifier = Modifier.weight(0.5f - TITLE_WEIGHT),
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
private fun ProfileInfoPreview() {
    PreviewLayout {
        ProfileInfoContent(characterProfileInfo = CharacterProfileInfo())
    }
}

@CombinedPreviews
@Composable
private fun HomePageCommentContentPreview() {
    PreviewLayout(horizontalAlignment = Alignment.CenterHorizontally) {
        val text = stringResource(id = R.string.debug_long_text)
        HomePageCommentContent(
            selfText = text,
            homePageCommentList = arrayListOf(CharacterHomePageComment(comments = text))
        )
    }

}

@CombinedPreviews
@Composable
private fun RoomCommentContentPreview() {
    PreviewLayout(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val text = stringResource(id = R.string.debug_long_text)
        RoomCommentContent(
            roomCommentList = arrayListOf(
                RoomCommentData(roomComment = text),
                RoomCommentData(roomComment = text)
            )
        )
    }
}