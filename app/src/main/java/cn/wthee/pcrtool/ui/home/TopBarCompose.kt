package cn.wthee.pcrtool.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.skill.ColorTextIndex
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.joinQQGroup
import cn.wthee.pcrtool.viewmodel.NoticeViewModel


/**
 * 顶部工具栏
 *
 */
@Composable
fun TopBarCompose(
    isEditMode: MutableState<Boolean>,
    noticeViewModel: NoticeViewModel
) {
    val updateApp = noticeViewModel.updateApp.observeAsState().value ?: AppNotice()
    var isExpanded by remember {
        mutableStateOf(false)
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(
                top = Dimen.largePadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding
            )
            .fillMaxWidth()
    ) {
        HeaderText(
            text = stringResource(id = R.string.app_name)
        )
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //应用更新
            if (updateApp.id == -1) {
                //校验更新中
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(Dimen.fabIconSize)
                        .padding(Dimen.smallPadding),
                    color = MaterialTheme.colorScheme.onSurface,
                    strokeWidth = 3.dp
                )
            } else {
                if (updateApp.id != -2) {
                    val updateColor =
                        if (updateApp.id == 0) colorGreen else MaterialTheme.colorScheme.onSurface
                    val icon =
                        if (updateApp.id == 0) MainIconType.APP_UPDATE else MainIconType.NOTICE
                    IconCompose(
                        data = if (isExpanded) MainIconType.CLOSE else icon,
                        tint = if (isExpanded) MaterialTheme.colorScheme.onSurface else updateColor,
                        size = Dimen.fabIconSize,
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    ) {
                        isExpanded = !isExpanded
                    }
                }
            }
            Spacer(modifier = Modifier.width(Dimen.largePadding))
            //编辑
            IconCompose(
                data = if (isEditMode.value) MainIconType.OK else MainIconType.EDIT_TOOL,
                tint = MaterialTheme.colorScheme.onSurface,
                size = Dimen.fabIconSize
            ) {
                isEditMode.value = !isEditMode.value
            }
            Spacer(modifier = Modifier.width(Dimen.largePadding))
        }

    }

    ExpandAnimation(visible = isExpanded) {
        AppUpdateContent(appNotice = updateApp)
    }
}

//应用更新内容
@Composable
private fun AppUpdateContent(appNotice: AppNotice) {
    val context = LocalContext.current

    val mark0 = arrayListOf<ColorTextIndex>()
    appNotice.message.forEachIndexed { index, c ->
        if (c == '[') {
            mark0.add(ColorTextIndex(start = index))
        }
        if (c == ']') {
            mark0[mark0.size - 1].end = index
        }
    }


    MainCard(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = Dimen.largePadding)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(top = Dimen.mediumPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //版本
                Text(
                    text = "v${appNotice.title}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )
                //反馈群
                IconTextButton(
                    icon = MainIconType.SUPPORT,
                    text = stringResource(id = R.string.qq_group),
                ) {
                    joinQQGroup(context)
                }
                //酷安
                IconTextButton(
                    icon = MainIconType.COOLAPK_APP_STORE,
                    text = stringResource(id = R.string.coolapk),
                ) {
                    BrowserUtil.open(appNotice.url)
                }
            }

            //日期
            CaptionText(
                text = stringResource(
                    id = R.string.release,
                    appNotice.date.formatTime.substring(0, 10)
                )
            )

            //内容
            Text(
                text = buildAnnotatedString {
                    appNotice.message.forEachIndexed { index, char ->
                        //替换括号及括号内字体颜色
                        mark0.forEach {
                            if (index >= it.start && index <= it.end) {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(char)
                                }
                                return@forEachIndexed
                            }
                        }
                        //添加非括号标记的参数
                        append(char)
                    }
                },
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(top = Dimen.largePadding, bottom = Dimen.mediumPadding),
                style = MaterialTheme.typography.bodyLarge,
            )

            //前往更新
            if (appNotice.id == 0) {
                TextButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    onClick = {
                        VibrateUtil(context).single()
                        BrowserUtil.open(appNotice.url)
                    }
                ) {
                    MainContentText(
                        text = stringResource(id = R.string.to_update),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}


@CombinedPreviews
@Composable
private fun AppUpdateContentPreview() {
    PreviewLayout {
        AppUpdateContent(
            AppNotice(
                date = "2022-01-01 01:01:01",
                title = "3.2.1",
                message = "- [BUG] BUGBUG\n- [测试] 测试",
                file_url = "123"
            )
        )
    }
}