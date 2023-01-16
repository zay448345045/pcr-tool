package cn.wthee.pcrtool.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.ui.MainActivity.Companion.animOnFlag

/**
 * 动画弹性
 */
fun <T> defaultSpring(): SpringSpec<T> {
    return spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )
}

/**
 * 持续时间
 */
fun <T> defaultTween(durationMillis: Int = 400): TweenSpec<T> {
    return tween(durationMillis = durationMillis, easing = FastOutSlowInEasing)
}

/**
 * 导航返回跳转动画
 */
val myExit = fadeOut(animationSpec = defaultTween(180))
val myPopExit = fadeOut(animationSpec = defaultTween(180))

/**
 * 页面退出动画
 */
val myFadeOut = fadeOut(animationSpec = defaultTween())

/**
 * 页面进入动画：从下向上滚动
 */
val mySlideIn = slideInVertically(
    initialOffsetY = { it },
    animationSpec = defaultSpring()
)

/**
 * 页面进入动画：渐入
 */
val myFadeIn = fadeIn(animationSpec = defaultTween())

/**
 * 展开
 */
val myExpandIn = expandVertically(
    expandFrom = Alignment.Top,
    animationSpec = defaultSpring()
) + fadeIn(animationSpec = defaultSpring())

/**
 * 折叠
 */
val myShrinkIn = shrinkVertically(
    shrinkTowards = Alignment.Top,
    animationSpec = defaultSpring()
) + fadeOut(animationSpec = defaultSpring())

/**
 * 页面进入动画
 */
@Composable
fun SlideAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = if (animOnFlag) mySlideIn else noAnimIn(),
        exit = if (animOnFlag) myFadeOut else noAnimOut(),
        content = content,
    )
}

/**
 * 页面淡入动画
 */
@Composable
fun FadeAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = if (animOnFlag) myFadeIn else noAnimIn(),
        exit = if (animOnFlag) myFadeOut else noAnimOut(),
        content = content,
        modifier = modifier
    )
}

/**
 * 页面展开动画，从上到下
 */
@Composable
fun ExpandAnimation(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = if (animOnFlag) myExpandIn else noAnimIn(),
        exit = if (animOnFlag) myShrinkIn else noAnimOut(),
        content = content,
    )
}

/**
 * 减弱进入动画效果
 */
private fun noAnimIn() = fadeIn(animationSpec = defaultTween(80))

/**
 * 减弱退出动画效果
 */
private fun noAnimOut() = fadeOut(animationSpec = defaultTween(80))
