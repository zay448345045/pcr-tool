package cn.wthee.pcrtool.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity.Companion.actions
import cn.wthee.pcrtool.ui.MainActivity.Companion.navController
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.MainActivity.Companion.noticeViewModel
import cn.wthee.pcrtool.ui.MainActivity.Companion.r6Ids
import cn.wthee.pcrtool.ui.compose.FabCompose
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PcrtoolcomposeTheme
import cn.wthee.pcrtool.utils.ActivityHelper
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.UMengInitializer
import cn.wthee.pcrtool.viewmodel.NoticeViewModel
import cn.wthee.pcrtool.viewmodel.OverviewViewModel
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 本地存储
 */
fun mainSP(): SharedPreferences =
    MyApplication.context.getSharedPreferences("main", Context.MODE_PRIVATE)!!

fun mainSP(context: Context): SharedPreferences =
    context.getSharedPreferences("main", Context.MODE_PRIVATE)!!


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        lateinit var handler: Handler
        lateinit var navViewModel: NavViewModel
        lateinit var noticeViewModel: NoticeViewModel
        lateinit var actions: NavActions
        var mFloatingWindowHeight = 0

        @SuppressLint("StaticFieldLeak")
        lateinit var navController: NavHostController
        var vibrateOn = true
        var animOn = true
        var r6Ids = listOf<Int>()
    }


    @ExperimentalComposeUiApi
    @ExperimentalPagingApi
    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    @ExperimentalCoilApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PcrtoolcomposeTheme {
                ProvideWindowInsets {
                    //状态栏、导航栏适配
                    val ui = rememberSystemUiController()
                    val isLight = MaterialTheme.colors.isLight
                    ui.setNavigationBarColor(
                        colorResource(id = if (isLight) R.color.alpha_white else R.color.alpha_black),
                        darkIcons = isLight
                    )
                    ui.setStatusBarColor(MaterialTheme.colors.background, darkIcons = isLight)
                    Surface(modifier = Modifier.background(MaterialTheme.colors.background)) {
                        Home()
                    }
                }
            }
        }
        ActivityHelper.instance.currentActivity = this
        //设置 handler
        setHandler()
        UMengInitializer().create(this)
        val sp = mainSP()
        vibrateOn = sp.getBoolean(Constants.SP_VIBRATE_STATE, true)
        animOn = sp.getBoolean(Constants.SP_ANIM_STATE, true)
        MainScope().launch {
            DatabaseUpdater.checkDBVersion()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val width = ScreenUtil.getWidth()
        val height = ScreenUtil.getHeight()
        mFloatingWindowHeight = if (width > height) height else width
        super.onConfigurationChanged(newConfig)
    }

    //返回拦截
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            navViewModel.loading.postValue(false)
            when (navViewModel.fabMainIcon.value ?: MainIconType.MAIN) {
                MainIconType.MAIN -> {
                    return super.onKeyDown(keyCode, event)
                }
                MainIconType.DOWN -> {
                    navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
                    return true
                }
                else -> {
                    navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    //刷新页面
    @SuppressLint("RestrictedApi")
    private fun setHandler() {
        //接收消息
        handler = Handler(Looper.getMainLooper(), Handler.Callback {
            savedStateRegistry.unregisterSavedStateProvider(OverviewViewModel::class.java.name)
            viewModelStore.clear()
            recreate()
            return@Callback true
        })
    }
}


@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalPagingApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@Composable
fun Home(
    mNavViewModel: NavViewModel = hiltViewModel(),
    mNoticeViewModel: NoticeViewModel = hiltViewModel()
) {
    navController = rememberNavController()
    actions = remember(navController) { NavActions(navController) }
    navViewModel = mNavViewModel
    noticeViewModel = mNoticeViewModel

    LaunchedEffect({}) {
        noticeViewModel.check()
    }
    val loading = navViewModel.loading.observeAsState().value ?: false
    val r6IdList = navViewModel.r6Ids.observeAsState()

    if (r6IdList.value != null) {
        r6Ids = r6IdList.value!!
    }
    val statusBarHeight =
        rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars
        ).calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = statusBarHeight)
    ) {
        NavGraph(navController, navViewModel, actions)
        //菜单
        MoreFabCompose(navViewModel)
        Column(modifier = Modifier.align(Alignment.BottomEnd)) {
            FabMain(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(Dimen.fabMargin)
            )
        }
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(Dimen.fabIconSize)
                    .align(Alignment.Center)
            )
        }
    }
}

@ExperimentalCoilApi
@ExperimentalAnimationApi
@Composable
fun FabMain(modifier: Modifier = Modifier) {
    val icon = navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.MAIN

    FabCompose(icon, modifier = modifier) {
        when (icon) {
            MainIconType.OK -> navViewModel.fabOKCilck.postValue(true)
            MainIconType.CLOSE -> navViewModel.fabCloseClick.postValue(true)
            MainIconType.MAIN -> navViewModel.fabMainIcon.postValue(MainIconType.DOWN)
            MainIconType.DOWN -> navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
            else -> {
                navController.navigateUp()
                navViewModel.loading.postValue(false)
            }
        }
    }
}

/**
 * 预览
 */
@Composable
fun PreviewBox(themeType: Int = 0, content: @Composable () -> Unit) {
    Column {
        if (themeType == 0 || themeType == 1) {
            PcrtoolcomposeTheme(darkTheme = false) {
                Surface(
                    modifier = Modifier.background(MaterialTheme.colors.background),
                    content = content
                )
            }
        }
        if (themeType == 0) {
            Spacer(
                modifier = Modifier.height(Dimen.largePadding)
            )
        }
        if (themeType == 0 || themeType == 2) {
            PcrtoolcomposeTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.background(MaterialTheme.colors.background),
                    content = content
                )
            }
        }
    }
}
