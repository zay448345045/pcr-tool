package cn.wthee.pcrtool.ui

import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.FixedScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication.Companion.useIpOnFlag
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.SettingSwitchType
import cn.wthee.pcrtool.data.preferences.SettingPreferencesKeys
import cn.wthee.pcrtool.ui.MainActivity.Companion.animOnFlag
import cn.wthee.pcrtool.ui.MainActivity.Companion.dynamicColorOnFlag
import cn.wthee.pcrtool.ui.MainActivity.Companion.vibrateOnFlag
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.HeaderText
import cn.wthee.pcrtool.ui.components.MainAlertDialog
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.SCALE_LOGO
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.joinQQGroup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 设置页面
 */
@Composable
fun MainSettings() {
    val context = LocalContext.current

    //缓存删除确认弹窗
    val openDialog = remember {
        mutableStateOf(false)
    }
    //图片缓存大小
    val imageCacheSize = remember {
        mutableStateOf(Pair("", 0))
    }
    LaunchedEffect(openDialog.value) {
        imageCacheSize.value = FileUtil.getMediaDirSize(context)
    }


    MainScaffold {
        Column(
            modifier = Modifier
                .padding(horizontal = Dimen.mediumPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(bottom = Dimen.largePadding)
                    .fillMaxWidth()
            ) {
                HeaderText(
                    text = stringResource(id = R.string.app_name) + " v" + BuildConfig.VERSION_NAME,
                    modifier = Modifier.padding(top = Dimen.mediumPadding)
                )
                MainIcon(
                    data = R.drawable.ic_launcher_foreground,
                    size = Dimen.largeIconSize,
                    modifier = Modifier.padding(Dimen.mediumPadding),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    contentScale = FixedScale(SCALE_LOGO)
                )
            }


            //其它设置
            Spacer(modifier = Modifier.padding(vertical = Dimen.mediumPadding))
            MainText(
                text = stringResource(id = R.string.app_setting),
                modifier = Modifier.padding(Dimen.largePadding)
            )
            //- 振动开关
            SettingSwitchCompose(type = SettingSwitchType.VIBRATE, showSummary = true)
            //- 动画效果
            SettingSwitchCompose(type = SettingSwitchType.ANIMATION, showSummary = true)
            //- 动态色彩，仅 Android 12 及以上可用
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S || BuildConfig.DEBUG) {
                SettingSwitchCompose(type = SettingSwitchType.DYNAMIC_COLOR, showSummary = true)
            }
            //- 使用ip访问
            SettingSwitchCompose(type = SettingSwitchType.USE_IP, showSummary = true)
            //- 清除图片缓存
            SettingCommonItem(
                iconType = MainIconType.DELETE,
                title = stringResource(id = R.string.clean_image_cache),
                summary = stringResource(id = R.string.tip_clean_cache),
                onClick = {
                    //清除缓存弹窗
                    openDialog.value = true
                }
            ) {
                Subtitle2(
                    text = stringResource(
                        id = R.string.cache_size,
                        imageCacheSize.value.first,
                        imageCacheSize.value.second
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            //其它相关
            Spacer(modifier = Modifier.padding(vertical = Dimen.mediumPadding))
            MainText(
                text = stringResource(id = R.string.other),
                modifier = Modifier.padding(Dimen.largePadding)
            )
            //- 加入反馈群
            SettingCommonItem(
                iconType = MainIconType.SUPPORT,
                title = stringResource(id = R.string.qq_group),
                summary = stringResource(id = R.string.qq_group_summary),
                onClick = {
                    joinQQGroup(context)
                }
            ) {
                Subtitle2(
                    text = stringResource(id = R.string.to_join_qq_group),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            //- 模型预览
            SettingCommonItem(
                iconType = MainIconType.PREVIEW_UNIT_SPINE,
                title = stringResource(id = R.string.title_spine),
                summary = stringResource(id = R.string.spine_tip),
                onClick = {
                    BrowserUtil.open(Constants.PREVIEW_URL)
                }
            )
            //- 爱发电
            val afdUrl = stringResource(id = R.string.afd_url)
            SettingCommonItem(
                iconType = MainIconType.SPONSOR,
                title = stringResource(id = R.string.afd),
                summary = stringResource(id = R.string.tip_afd),
                onClick = {
                    BrowserUtil.open(afdUrl)
                }
            ) {
                Subtitle2(
                    text = stringResource(id = R.string.please),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            //- 项目代码
            val gitUrl = stringResource(id = R.string.github_project_url)
            SettingCommonItem(
                iconType = MainIconType.GITHUB_PROJECT,
                title = stringResource(id = R.string.github),
                summary = stringResource(id = R.string.tip_github),
                onClick = {
                    BrowserUtil.open(gitUrl)
                }
            )

            //感谢友链
            Spacer(modifier = Modifier.padding(vertical = Dimen.mediumPadding))
            MainText(
                text = stringResource(id = R.string.thanks),
                modifier = Modifier.padding(Dimen.largePadding)
            )
            //- 干炸里脊资源
            val dataFromUrl = stringResource(id = R.string.data_from_url)
            SettingCommonItem(
                iconType = MainIconType.DATA_SOURCE,
                title = stringResource(id = R.string.data_from),
                summary = stringResource(id = R.string.data_from_hint),
                onClick = {
                    BrowserUtil.open(dataFromUrl)
                }
            )
            //- 静流笔记
            val shizuruUrl = stringResource(id = R.string.shizuru_note_url)
            SettingCommonItem(
                iconType = MainIconType.NOTE,
                title = stringResource(id = R.string.shizuru_note),
                summary = stringResource(id = R.string.shizuru_note_tip),
                onClick = {
                    BrowserUtil.open(shizuruUrl)
                }
            )
            //- 竞技场
            val pcrdfansUrl = stringResource(id = R.string.pcrdfans_url)
            SettingCommonItem(
                iconType = MainIconType.PVP_SEARCH,
                title = stringResource(id = R.string.pcrdfans),
                summary = stringResource(id = R.string.pcrdfans_tip),
                onClick = {
                    BrowserUtil.open(pcrdfansUrl)
                }
            )
            //- 排行
            val leaderUrl = stringResource(id = R.string.leader_source_url)
            SettingCommonItem(
                iconType = MainIconType.LEADER,
                title = stringResource(id = R.string.leader_source),
                summary = stringResource(id = R.string.leader_tip),
                onClick = {
                    BrowserUtil.open(leaderUrl)
                }
            )
            //- 漫画汉化
            val comicZhUrl = stringResource(id = R.string.comic_zh_url)
            SettingCommonItem(
                iconType = MainIconType.COMIC,
                title = stringResource(id = R.string.title_comic_zh),
                summary = stringResource(id = R.string.tip_comic_zh),
                onClick = {
                    BrowserUtil.open(comicZhUrl)
                }
            )
            //- 日服情报
            val jpInfoUrl = stringResource(id = R.string.jp_info_url)
            SettingCommonItem(
                iconType = MainIconType.TWEET,
                title = stringResource(id = R.string.title_jp_info),
                summary = stringResource(id = R.string.tip_jp_info),
                onClick = {
                    BrowserUtil.open(jpInfoUrl)
                }
            )
            //- 额外掉落
            val randomDropUrl = stringResource(id = R.string.random_drop_source_url)
            SettingCommonItem(
                iconType = MainIconType.RANDOM_AREA,
                title = stringResource(id = R.string.random_drop_source),
                summary = stringResource(id = R.string.random_drop_tip),
                onClick = {
                    BrowserUtil.open(randomDropUrl)
                }
            )
            CommonSpacer()
        }
    }


    //弹窗确认
    MainAlertDialog(
        openDialog = openDialog,
        icon = MainIconType.DELETE,
        title = stringResource(id = R.string.title_dialog_clean_cache),
        text = stringResource(id = R.string.confirm_clean_cache)
    ) {
        FileUtil.delete(context.filesDir.resolve(Constants.COIL_DIR))
        FileUtil.delete(context.filesDir.resolve(Constants.VIDEO_DIR))
    }

}

/**
 * 设置项（带开关）
 *
 * @param type 设置类型 [cn.wthee.pcrtool.data.enums.SettingSwitchType]
 * @param showSummary 是否显示摘要，主页面设置为 false，设置页面设置为 true
 */
@Composable
fun SettingSwitchCompose(
    modifier: Modifier = Modifier,
    type: SettingSwitchType,
    showSummary: Boolean,
    wrapWidth: Boolean = false,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val title: String
    val iconType: MainIconType
    val spKey: Preferences.Key<Boolean>
    val summaryOn: String
    val summaryOff: String

    //设置
    when (type) {
        SettingSwitchType.VIBRATE -> {
            title = stringResource(id = R.string.vibrate)
            iconType = MainIconType.VIBRATE
            summaryOn = if (showSummary) stringResource(R.string.vibrate_on) else ""
            summaryOff = if (showSummary) stringResource(R.string.vibrate_off) else ""
            spKey = SettingPreferencesKeys.SP_VIBRATE_STATE
        }

        SettingSwitchType.ANIMATION -> {
            title = stringResource(id = R.string.animation)
            iconType = MainIconType.ANIMATION
            summaryOn = if (showSummary) stringResource(R.string.animation_on) else ""
            summaryOff = if (showSummary) stringResource(R.string.animation_off) else ""
            spKey = SettingPreferencesKeys.SP_ANIM_STATE
        }

        SettingSwitchType.DYNAMIC_COLOR -> {
            title = stringResource(id = R.string.dynamic_color)
            iconType = MainIconType.COLOR
            summaryOn = if (showSummary) stringResource(R.string.color_on) else ""
            summaryOff = if (showSummary) stringResource(R.string.color_off) else ""
            spKey = SettingPreferencesKeys.SP_COLOR_STATE
        }

        SettingSwitchType.USE_IP -> {
            title = stringResource(id = R.string.use_ip)
            iconType = MainIconType.REQUEST_ERROR
            summaryOn = if (showSummary) stringResource(R.string.use_ip_tip_on) else ""
            summaryOff = if (showSummary) stringResource(R.string.use_ip_tip_off) else ""
            spKey = SettingPreferencesKeys.SP_USE_IP
        }
    }

    val spValue = runBlocking {
        context.dataStoreSetting.data.first()[spKey] ?: (spKey != SettingPreferencesKeys.SP_USE_IP)
    }
    var checkedState by remember {
        mutableStateOf(spValue)
    }

    //更新flag
    SideEffect {
        when (type) {
            SettingSwitchType.VIBRATE -> {
                vibrateOnFlag = checkedState
            }

            SettingSwitchType.ANIMATION -> {
                animOnFlag = checkedState

            }

            SettingSwitchType.DYNAMIC_COLOR -> {
                dynamicColorOnFlag = checkedState
            }

            SettingSwitchType.USE_IP -> {
                useIpOnFlag = checkedState
            }
        }
    }

    //摘要
    val summary = if (checkedState) summaryOn else summaryOff

    val onChange = {
        checkedState = !checkedState
        scope.launch {
            //保存设置信息
            context.dataStoreSetting.edit {
                it[spKey] = checkedState
            }

            //动态色彩变更后，重启应用
            if (type == SettingSwitchType.DYNAMIC_COLOR) {
                MainActivity.handler.sendEmptyMessage(1)
            }
            //ip变更，结束应用
            if (type == SettingSwitchType.USE_IP) {
                MainActivity.handler.sendEmptyMessage(404)
            }
        }
    }

    SettingCommonItem(
        modifier = modifier,
        iconType = iconType,
        title = title,
        summary = summary,
        wrapWidth = wrapWidth,
        onClick = {
            onChange()
        }
    ) {
        Switch(
            checked = checkedState,
            thumbContent = {
                SwitchThumbIcon(checkedState)
            },
            onCheckedChange = {
                VibrateUtil(context).single()
                onChange()
            }
        )
    }
}

/**
 * 设置子项
 *
 * @param iconType      图标
 * @param iconSize      图标大小
 * @param title         标题
 * @param summary       摘要
 * @param titleColor    标题颜色
 * @param summaryColor  摘要颜色
 * @param onClick       点击事件
 * @param extraContent  右侧额外内容
 * @param wrapWidth     自适应宽度
 */
@Composable
fun SettingCommonItem(
    modifier: Modifier = Modifier,
    iconType: Any,
    iconSize: Dp = Dimen.menuIconSize,
    title: String,
    summary: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    summaryColor: Color = MaterialTheme.colorScheme.outline,
    padding: Dp = Dimen.largePadding,
    tintColor: Color = MaterialTheme.colorScheme.primary,
    wrapWidth: Boolean = false,
    onClick: () -> Unit,
    extraContent: (@Composable RowScope.() -> Unit)? = null
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = {
                VibrateUtil(context).single()
                onClick()
            })
    ) {
        Spacer(modifier = Modifier.width(padding))
        MainIcon(
            data = iconType,
            size = iconSize,
            colorFilter = ColorFilter.tint(tintColor),
            contentScale = FixedScale(SCALE_LOGO)
        )
        Column(
            modifier = if (wrapWidth) {
                Modifier.padding(padding)
            } else {
                Modifier
                    .weight(1f)
                    .padding(padding)
            },
            verticalArrangement = Arrangement.Center
        ) {
            Subtitle1(text = title, color = titleColor)
            if (summary != "") {
                Subtitle2(
                    text = summary,
                    modifier = Modifier.padding(top = Dimen.mediumPadding),
                    color = summaryColor
                )
            }
        }
        extraContent?.let {
            extraContent()
        }
        Spacer(modifier = Modifier.width(padding))
    }
}

/**
 * switch 选中图标
 */
@Composable
private fun SwitchThumbIcon(checked: Boolean) {
    Icon(
        imageVector = if (checked) MainIconType.OK.icon else MainIconType.CLOSE.icon,
        contentDescription = "",
        modifier = Modifier.size(SwitchDefaults.IconSize)
    )
}


@CombinedPreviews
@Composable
private fun SettingPreview() {
    PreviewLayout {
        SettingSwitchCompose(type = SettingSwitchType.VIBRATE, showSummary = true)
        SettingCommonItem(
            iconType = MainIconType.SUPPORT,
            title = stringResource(id = R.string.qq_group),
            summary = stringResource(id = R.string.qq_group_summary),
            onClick = {}
        )
    }
}