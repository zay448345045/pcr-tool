package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterHomePageComment
import cn.wthee.pcrtool.data.db.view.CharacterInfoPro
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.copyText
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.getFixed
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

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
    val data = viewModel.getCharacter(unitId).collectAsState(initial = null).value
    val homePageCommentList =
        viewModel.getHomePageComments(unitId).collectAsState(initial = arrayListOf()).value

    Box(modifier = Modifier.fillMaxSize()) {
        data?.let { info ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
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
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.character),
                modifier = Modifier.weight(0.15f)
            )
            MainContentText(
                text = info.unitName,
                modifier = Modifier.weight(0.85f),
                selectable = true
            )
        }
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.name),
                modifier = Modifier.weight(0.15f)
            )
            MainContentText(
                text = info.actualName,
                modifier = Modifier.weight(0.85f),
                selectable = true
            )
        }
        //身高、体重
        TwoColumnsInfo(
            stringResource(id = R.string.title_height),
            getFixed(info.height) + " CM",
            stringResource(id = R.string.title_weight),
            getFixed(info.weight) + " KG"
        )
        //生日、年龄
        TwoColumnsInfo(
            stringResource(id = R.string.title_birth),
            stringResource(id = R.string.date_m_d, getFixed(info.birthMonth) , getFixed(info.birthDay)),
            stringResource(id = R.string.age),
            getFixed(info.age)
        )
        //血型、位置
        TwoColumnsInfo(
            stringResource(id = R.string.title_blood),
            info.bloodType,
            stringResource(id = R.string.cv),
            info.voice
        )
        //种族
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_race),
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        MainContentText(
            text = info.race,
            modifier = Modifier.padding(Dimen.mediumPadding),
            textAlign = TextAlign.Start,
            selectable = true
        )
        //公会
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_guild),
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        MainContentText(
            text = info.guild,
            modifier = Modifier.padding(Dimen.mediumPadding),
            textAlign = TextAlign.Start,
            selectable = true
        )
        //兴趣
        Row(modifier = Modifier.padding(top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_fav),
                modifier = Modifier.weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        MainContentText(
            text = info.favorite,
            modifier = Modifier.padding(Dimen.mediumPadding),
            textAlign = TextAlign.Start,
            selectable = true
        )
    }
}

/**
 * 主页交流信息
 */
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
        CommentTextCard(text = it)
    }

    //主页交流
    homePageCommentList.forEach { comment ->
        Row(modifier = Modifier.padding(start = Dimen.largePadding, top = Dimen.mediumPadding)) {
            MainTitleText(
                text = stringResource(id = R.string.title_comments),
                modifier = Modifier.weight(0.15f)
            )
            MainTitleText(
                text = "★" + if (comment.unitId % 100 / 10 == 0) {
                    "1"
                } else {
                    "${comment.unitId % 100 / 10}"
                },
                modifier = Modifier
                    .padding(start = Dimen.smallPadding)
                    .weight(0.15f)
            )
            Spacer(modifier = Modifier.weight(0.85f))
        }
        comment.getCommentList().forEach {
            CommentTextCard(it)
        }
    }

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
 * 小屋交流文本
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun RoomComment(unitId: Int, viewModel: CharacterViewModel) {
    val roomComments =
        viewModel.getRoomComments(unitId).collectAsState(initial = null).value
    val pagerState = rememberPagerState()


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
        Spacer(modifier = Modifier.weight(0.85f))
    }
    roomComments?.let {
        //多角色时，显示角色图标
        if (roomComments.size > 1) {
            val urls = arrayListOf<String>()
            roomComments.forEach { roomComment ->
                urls.add(
                    ImageResourceHelper.getInstance().getMaxIconUrl(roomComment.unitId)
                )
            }
            IconHorizontalPagerIndicator(pagerState, urls)
        }
        HorizontalPager(
            state = pagerState,
            count = roomComments.size,
            verticalAlignment = Alignment.Top
        ) { index ->
            Column {
                roomComments[index].getCommentList().forEach {
                    CommentTextCard(it)
                }
                CommonSpacer()
            }
        }
    }
}

/**
 * 文本卡片
 */
@Composable
private fun CommentTextCard(text: String) {
    val context = LocalContext.current
    MainCard(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        ),
        onClick = {
            copyText(context, text)
        }
    ) {
        MainContentText(
            text = text,
            modifier = Modifier.padding(Dimen.mediumPadding),
            textAlign = TextAlign.Start
        )
    }
}

/**
 * 角色基本信息预览
 */
@CombinedPreviews
@Composable
private fun BasicInfoPreview() {
    PreviewLayout{
        BasicInfo(info = CharacterInfoPro())
    }
}