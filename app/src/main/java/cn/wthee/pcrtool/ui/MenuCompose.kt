package cn.wthee.pcrtool.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.compose.FabCompose
import cn.wthee.pcrtool.ui.compose.MenuAnimation
import cn.wthee.pcrtool.ui.compose.defaultSpring
import cn.wthee.pcrtool.ui.compose.defaultTween
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.addToClip
import cn.wthee.pcrtool.utils.vibrate
import cn.wthee.pcrtool.viewmodel.NoticeViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

enum class MenuState {
    DEFAULT, TOUCH
}

/**
 * 菜单
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MenuContent(
    viewModel: NavViewModel,
    actions: NavActions,
    noticeViewModel: NoticeViewModel = hiltViewModel()
) {
    val fabMainIcon = viewModel.fabMainIcon.observeAsState().value ?: MainIconType.OK
    val coroutineScope = rememberCoroutineScope()
    val updateApp = noticeViewModel.updateApp.observeAsState().value ?: false

    val backgroundAnim = animateFloatAsState(
        targetValue = if (fabMainIcon == MainIconType.DOWN) 1f else 0f,
        animationSpec = defaultTween()
    )
    val systemUiController = rememberSystemUiController()
    val colorAnim =
        animateColorAsState(
            targetValue = if (fabMainIcon == MainIconType.DOWN) {
                Color.Transparent
            } else {
                MaterialTheme.colors.background
            }, animationSpec = defaultTween()
        )
    systemUiController.setStatusBarColor(colorAnim.value)


    Box(modifier = Modifier
        .fillMaxSize()
        .alpha(backgroundAnim.value)
        .background(colorResource(id = R.color.alpha_black))
        .clickable(enabled = fabMainIcon == MainIconType.DOWN) {
            viewModel.fabMainIcon.postValue(MainIconType.MAIN)
        }
    ) {
        MenuAnimation(visible = fabMainIcon == MainIconType.DOWN) {
            Column(verticalArrangement = Arrangement.Bottom, modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.height(Dimen.largeMenuHeight)) {
                    //卡池
                    MenuItem(
                        text = stringResource(id = R.string.tool_gacha),
                        iconType = MainIconType.GACHA,
                        modifier = Modifier
                            .weight(0.382f)
                            .height(Dimen.largeMenuHeight),
                    ) {
                        actions.toGacha()
                    }
                    //团队战
                    MenuItem(
                        text = stringResource(id = R.string.tool_clan),
                        iconType = MainIconType.CLAN,
                        modifier = Modifier
                            .weight(0.382f)
                            .height(Dimen.largeMenuHeight)
                    ) {
                        actions.toClan()
                    }
                    //剧情活动
                    Column(modifier = Modifier.weight(0.618f)) {
                        MenuItem(
                            text = stringResource(id = R.string.tool_event),
                            iconType = MainIconType.EVENT,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f)
                        ) {
                            actions.toEvent()
                        }
                        MenuItem(
                            text = stringResource(id = R.string.tool_guild),
                            iconType = MainIconType.GUILD,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f)
                        ) {
                            actions.toGuild()
                        }
                    }
                }

                Row(modifier = Modifier.height(Dimen.largeMenuHeight + Dimen.menuHeight)) {

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.5f)
                    ) {
                        //官网公告
                        MenuItem(
                            text = stringResource(id = R.string.tool_news_cn),
                            iconType = MainIconType.NEWS,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            actions.toNews(2)
                        }
                        //官网公告
                        MenuItem(
                            text = stringResource(id = R.string.tool_news_tw),
                            iconType = MainIconType.NEWS,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            actions.toNews(3)
                        }
                        //官网公告
                        MenuItem(
                            text = stringResource(id = R.string.tool_news_jp),
                            iconType = MainIconType.NEWS,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            actions.toNews(4)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.5f)
                    ) {
                        //竞技场
                        MenuItem(
                            text = stringResource(id = R.string.tool_pvp),
                            iconType = MainIconType.PVP_SEARCH,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.6f)
                        ) {
                            actions.toPvp()
                        }

                        //日历
                        MenuItem(
                            text = stringResource(id = R.string.tool_calendar),
                            iconType = MainIconType.CALENDAR,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.3f)
                        ) {
                            actions.toCalendar()
                        }

                    }

                }

                Row(modifier = Modifier.height(Dimen.largeMenuHeight)) {
                    //排行
                    MenuItem(
                        text = stringResource(id = R.string.tool_leader),
                        iconType = MainIconType.LEADER,
                        modifier = Modifier
                            .weight(0.3f)
                            .fillMaxHeight()
                    ) {
                        actions.toLeader()
                    }
                    //装备
                    MenuItem(
                        text = stringResource(id = R.string.tool_equip),
                        iconType = MainIconType.EQUIP,
                        modifier = Modifier
                            .weight(0.38f)
                            .fillMaxHeight()
                    ) {
                        actions.toEquipList()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.32f)
                    ) {
                        //通知
                        MenuItem(
                            backgroundColor = if (updateApp == 1) colorResource(id = R.color.color_rank_21) else MaterialTheme.colors.primary,
                            text = stringResource(id = if (updateApp == 1) R.string.to_update else R.string.app_notice),
                            iconType = if (updateApp == 1) MainIconType.APP_UPDATE else MainIconType.NOTICE,
                            modifier = Modifier
                                .weight(0.5f)
                                .fillMaxWidth()
                        ) {
                            actions.toNotice()
                        }
                        //设置
                        MenuItem(
                            text = stringResource(id = R.string.setting),
                            iconType = MainIconType.SETTING,
                            modifier = Modifier
                                .weight(0.5f)
                                .fillMaxWidth()
                        ) {
                            actions.toSetting()
                        }
                    }

                }

                Row(
                    modifier = Modifier
                        .padding(
                            top = Dimen.mediuPadding,
                            bottom = Dimen.fabMargin,
                            end = Dimen.fabMarginEnd
                        )
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    val qqGroup = stringResource(R.string.qq_group)
                    val tip = stringResource(R.string.copy_qq_tip)
                    //群
                    FabCompose(
                        iconType = MainIconType.GROUP,
                        text = stringResource(R.string.feedback),
                        modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                    ) {
                        addToClip(qqGroup, tip)
                    }
                    //数据版本切换
                    FabCompose(
                        iconType = MainIconType.CHANGE_DATA,
                        text = stringResource(id = R.string.change_db)
                    ) {
                        coroutineScope.launch {
                            viewModel.fabMainIcon.postValue(MainIconType.MAIN)
                            DatabaseUpdater.changeType()
                        }
                    }
                }
            }
        }
    }
}

/**
 * 菜单项
 */
@ExperimentalMaterialApi
@Composable
fun MenuItem(
    backgroundColor: Color = MaterialTheme.colors.primary,
    text: String,
    iconType: MainIconType,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val state = remember {
        mutableStateOf(MenuState.DEFAULT)
    }
    val scaleAnimation = animateFloatAsState(
        targetValue = if (state.value == MenuState.TOUCH) 0.85f else 1f, defaultSpring()
    )
    val context = LocalContext.current

    Card(
        backgroundColor = backgroundColor,
        onClick = onClick.vibrate {
            VibrateUtil(context).single()
        },
        contentColor = MaterialTheme.colors.onSurface,
        modifier = modifier
            .padding(Dimen.mediuPadding)
            .scale(scaleAnimation.value)
//            .pointerInput(Unit) {
//                detectTapGestures(
//                    onPress = {
//                        scope.launch {
//                            state.value = MenuState.TOUCH
//                            delay(50L)
//                            state.value = MenuState.DEFAULT
//                            VibrateUtil(context).single()
//                            delay(50L)
//                        }
//                    },
//                )
//            }
    ) {
        Box {
            Text(
                text = text,
                color = MaterialTheme.colors.onPrimary,
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(Dimen.mediuPadding)
                    .align(Alignment.TopStart)
            )
            Icon(
                iconType.icon,
                contentDescription = null,
                tint = MaterialTheme.colors.onPrimary,
                modifier = Modifier
                    .padding(Dimen.mediuPadding)
                    .size(Dimen.iconSize)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}
