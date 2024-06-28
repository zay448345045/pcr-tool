package cn.wthee.pcrtool.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.SettingSwitchType
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.SettingCommonItem
import cn.wthee.pcrtool.ui.SettingSwitchCompose
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CircularProgressCompose
import cn.wthee.pcrtool.ui.components.ColorText
import cn.wthee.pcrtool.ui.components.HeaderText
import cn.wthee.pcrtool.ui.components.IconTextButton
import cn.wthee.pcrtool.ui.components.LinearProgressCompose
import cn.wthee.pcrtool.ui.components.MainButton
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.SubButton
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.Constants.DOWNLOAD_APK_NAME
import cn.wthee.pcrtool.utils.FileUtil
import cn.wthee.pcrtool.utils.ToastUtil
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.joinQQGroup
import cn.wthee.pcrtool.workers.FileDownloadWorker
import java.io.File


/**
 * 顶部工具栏
 *
 * @param isEditMode 是否编辑模式
 * @param apkDownloadState 应用下载状态 [cn.wthee.pcrtool.ui.home.OverviewScreenUiState.apkDownloadState]
 * @param appUpdateData 应用更新内容
 * @param isExpanded 是否展开
 * @param changeEditMode 变更编辑模式 [cn.wthee.pcrtool.ui.home.OverviewScreenViewModel.changeEditMode]
 * @param updateApkDownloadState 更新下载状态 [cn.wthee.pcrtool.ui.home.OverviewScreenViewModel.updateApkDownloadState]
 * @param updateExpanded 更新展开状态 [cn.wthee.pcrtool.ui.home.OverviewScreenViewModel.updateExpanded]
 */
@Composable
fun TopBarCompose(
    isEditMode: Boolean,
    apkDownloadState: Int,
    appUpdateData: AppNotice,
    isExpanded: Boolean,
    changeEditMode: () -> Unit,
    updateApkDownloadState: (Int) -> Unit,
    updateExpanded: (Boolean) -> Unit
) {

    //Toolbar
    if (apkDownloadState <= -2) {
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

            //异常时显示版本号
            if (appUpdateData.id == -2) {
                CaptionText(text = "v" + BuildConfig.VERSION_NAME)
            }

            //数据版本，测试用
            if (BuildConfig.DEBUG) {
                CaptionText(text = MainActivity.regionType.name)
            }

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //应用更新
                when (appUpdateData.id) {
                    -1 -> {
                        //校验更新中
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(Dimen.fabIconSize)
                                .padding(Dimen.smallPadding),
                            color = MaterialTheme.colorScheme.onSurface,
                            strokeWidth = Dimen.strokeWidth
                        )
                    }

                    -2 -> {
                        //异常
                        MainIcon(
                            data = MainIconType.REQUEST_ERROR,
                            tint = colorRed,
                            size = Dimen.fabIconSize,
                            modifier = Modifier.padding(start = Dimen.smallPadding),
                            onClick = {
                                updateExpanded(!isExpanded)
                            }
                        )
                    }

                    else -> {
                        //提示
                        if (appUpdateData.id == 0 && !isExpanded) {
                            IconTextButton(
                                icon = MainIconType.APP_UPDATE,
                                text = stringResource(
                                    R.string.find_new_release,
                                    appUpdateData.title
                                ),
                                contentColor = colorGreen,
                                iconSize = Dimen.fabIconSize,
                                onClick = {
                                    updateExpanded(true)
                                }
                            )
                        } else {
                            MainIcon(
                                data = if (isExpanded) MainIconType.CLOSE else MainIconType.NOTICE,
                                tint = MaterialTheme.colorScheme.onSurface,
                                size = Dimen.fabIconSize,
                                modifier = Modifier.padding(start = Dimen.smallPadding),
                                onClick = {
                                    updateExpanded(!isExpanded)
                                }
                            )
                        }

                    }
                }
                Spacer(modifier = Modifier.width(Dimen.largePadding))
                //编辑
                MainIcon(
                    data = if (isEditMode) MainIconType.OK else MainIconType.EDIT_TOOL,
                    tint = MaterialTheme.colorScheme.onSurface,
                    size = Dimen.fabIconSize,
                    onClick = {
                        changeEditMode()
                    }
                )
            }
        }
    }

    //更新卡片布局
    ExpandAnimation(visible = isExpanded || appUpdateData.id == -2 || apkDownloadState > -2) {
        AppUpdateContent(
            appUpdateData = appUpdateData,
            apkDownloadState = apkDownloadState,
            updateApkDownloadState = updateApkDownloadState
        )
    }
}

/**
 * 应用更新内容或异常提示
 */
@Composable
private fun AppUpdateContent(
    appUpdateData: AppNotice,
    apkDownloadState: Int,
    updateApkDownloadState: (Int) -> Unit
) {
    val downloading = apkDownloadState > -2
    //下载、安装状态通知
    when (apkDownloadState) {
        -3 -> {
            ToastUtil.long(stringResource(R.string.download_apk_error))
            updateApkDownloadState(-2)
        }

        -4 -> {
            ToastUtil.long(stringResource(R.string.install_apk_error))
            updateApkDownloadState(-2)
        }
    }


    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(defaultSpring())
    ) {
        if (downloading) {
            HeaderText(
                text = stringResource(id = R.string.app_name),
                modifier = Modifier
                    .padding(
                        top = Dimen.largePadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    )
            )
            Spacer(modifier = Modifier.weight(1f))
            //下载进度
            if (apkDownloadState > -1) {
                CaptionText(
                    text = "$apkDownloadState%",
                    modifier = Modifier.padding(
                        top = Dimen.largePadding
                    )
                )
            }

        }

        MainCard(
            modifier = Modifier.padding(
                top = Dimen.largePadding,
                bottom = if (downloading) 0.dp else Dimen.mediumPadding,
                start = Dimen.largePadding,
                end = Dimen.largePadding,
            ),
            fillMaxWidth = !downloading
        ) {
            if (appUpdateData.id != -2) {
                if (downloading) {
                    //下载相关
                    DownloadingContent(apkDownloadState)
                } else {
                    if (appUpdateData.id == -1) {
                        //加载中
                        CircularProgressCompose(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(Dimen.mediumPadding)
                        )
                    } else {
                        //正常展示更新内容
                        UpdateContent(appUpdateData, updateApkDownloadState)
                    }
                }
            } else {
                //异常
                ErrorContent()
            }
        }
    }

}

/**
 * 下载进度
 */
@Composable
private fun DownloadingContent(
    apkDownloadState: Int
) {
    when (apkDownloadState) {
        -1, 100 -> {
            LinearProgressCompose(
                modifier = Modifier.fillMaxWidth(1 - RATIO_GOLDEN),
                color = colorGreen
            )
        }

        in 0..99 -> {
            LinearProgressCompose(
                modifier = Modifier.fillMaxWidth(1 - RATIO_GOLDEN),
                progress = apkDownloadState / 100f,
                color = colorGreen
            )
        }
    }
}

/**
 * 更新内容
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UpdateContent(
    appNotice: AppNotice,
    updateApkDownloadState: (Int) -> Unit
) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.smallPadding
        )
    ) {
        //更新内容
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            //版本
            Text(
                text = "v${appNotice.title}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.weight(1f))
            //项目地址
            val gitUrl = stringResource(id = R.string.github_project_url)
            IconTextButton(
                icon = MainIconType.GITHUB_PROJECT,
                text = stringResource(id = R.string.github),
                onClick = {
                    BrowserUtil.open(gitUrl)
                }
            )
            //爱发电
            val afdUrl = stringResource(id = R.string.afd_url)
            IconTextButton(
                icon = MainIconType.SPONSOR,
                text = stringResource(id = R.string.sponsor),
                onClick = {
                    BrowserUtil.open(afdUrl)
                }
            )
            //反馈群
            IconTextButton(
                icon = MainIconType.SUPPORT,
                text = stringResource(id = R.string.to_feedback),
                onClick = {
                    joinQQGroup(context)
                }
            )
        }

        //日期
        CaptionText(
            text = stringResource(
                id = R.string.release, appNotice.date.formatTime
            )
        )

        //内容
        ColorText(text = appNotice.message)

        //前往更新
        if (appNotice.id == 0) {
            //github下载链接
            val githubReleaseUrl = stringResource(id = R.string.apk_url, appNotice.title)

            FlowRow(
                modifier = Modifier
                    .padding(
                        top = Dimen.largePadding,
                        start = Dimen.largePadding,
                        end = Dimen.largePadding
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                //从GitHub下载
                SubButton(
                    text = stringResource(id = R.string.download_apk_from_github),
                    onClick = {
                        downloadApk(githubReleaseUrl, context, updateApkDownloadState, owner)
                    }
                )

                //从服务器下载
                MainButton(
                    text = stringResource(id = R.string.download_apk),
                    containerColor = colorGreen,
                    onClick = {
                        downloadApk(appNotice.url, context, updateApkDownloadState, owner)
                    }
                )
            }
        }

    }

}

/**
 * 下载文件
 */
private fun downloadApk(
    url: String,
    context: Context,
    updateApkDownloadState: (Int) -> Unit,
    lifecycleOwner: LifecycleOwner
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        BrowserUtil.open(url)
    } else {
        //请求应用安装权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                ToastUtil.long(getString(R.string.request_install))
                val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.data =
                    Uri.parse(
                        String.format(
                            "package:%s",
                            context.packageName
                        )
                    )
                context.startActivity(intent)
                return
            }
        }

        //准备下载
        updateApkDownloadState(-1)
        //下载
        val data = Data.Builder()
            .putString(FileDownloadWorker.KEY_URL, url)
            .putString(FileDownloadWorker.KEY_FILE_NAME, DOWNLOAD_APK_NAME)
            .build()
        val updateApkRequest =
            OneTimeWorkRequestBuilder<FileDownloadWorker>()
                .setInputData(data)
                .build()

        val workManager = WorkManager.getInstance(MyApplication.context)
        workManager.enqueueUniqueWork(
            Constants.DOWNLOAD_APK_WORK,
            ExistingWorkPolicy.KEEP,
            updateApkRequest
        )

        //监听下载进度
        workManager.getWorkInfoByIdLiveData(updateApkRequest.id)
            .observe(lifecycleOwner) { workInfo: WorkInfo? ->
                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            //下载成功，安装应用
                            val file =
                                FileUtil.getDownloadDir() + File.separator + DOWNLOAD_APK_NAME
                            openAPK(MyApplication.context, File(file))
                            updateApkDownloadState(-2)
                        }

                        WorkInfo.State.RUNNING -> {
                            val value = workInfo.progress.getInt(Constants.KEY_PROGRESS, -1)
                            updateApkDownloadState(value)
                        }

                        WorkInfo.State.FAILED -> {
                            updateApkDownloadState(-3)
                        }

                        else -> Unit
                    }
                }
            }
    }
}


/**
 * 异常内容
 */
@Composable
private fun ErrorContent() {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(Dimen.mediumPadding)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainText(
            text = stringResource(id = R.string.title_api_request_error),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        //内容
        ColorText(text = stringResource(id = R.string.content_api_request_error))

        //网络异常设置
        SettingSwitchCompose(type = SettingSwitchType.USE_IP, showSummary = true)

        //加群反馈
        SettingCommonItem(iconType = MainIconType.SUPPORT,
            title = stringResource(id = R.string.qq_group),
            summary = stringResource(id = R.string.qq_group_summary),
            onClick = {
                joinQQGroup(context)
            }) {
            Subtitle2(
                text = stringResource(id = R.string.to_join_qq_group),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}


/**
 * 安装apk
 */
private fun openAPK(context: Context, apkFile: File) {
    val auth = BuildConfig.APPLICATION_ID + ".provider"
    val type = "application/vnd.android.package-archive"

    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.setDataAndType(
        FileProvider.getUriForFile(
            context,
            auth,
            apkFile
        ),
        type
    )
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    context.startActivity(intent)
}

@CombinedPreviews
@Composable
private fun TopBarComposePreview() {
    PreviewLayout {
        //下载中
        TopBarCompose(
            isEditMode = false,
            appUpdateData = AppNotice(),
            apkDownloadState = 22,
            isExpanded = false,
            updateApkDownloadState = {},
            changeEditMode = {},
            updateExpanded = {},
        )
        //正常
        TopBarCompose(
            isEditMode = false,
            appUpdateData = AppNotice(
                id = 0,
                date = "2022-01-01 01:01:01",
                title = "3.2.1",
                message = "- [BUG] BUG\n- [测试] 测试",
                file_url = "123"
            ),
            apkDownloadState = -2,
            isExpanded = true,
            updateApkDownloadState = {},
            changeEditMode = {},
            updateExpanded = {},
        )
    }
}