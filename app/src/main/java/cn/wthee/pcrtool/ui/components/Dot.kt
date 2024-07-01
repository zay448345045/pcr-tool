package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.ui.theme.Dimen

/**
 * 圆点
 */
@Composable
fun Dot(color: Color, size: Dp = Dimen.indicatorSize) {
    Box(
        modifier = Modifier
            .padding(
                start = Dimen.exSmallPadding,
                end = Dimen.exSmallPadding,
                top = Dimen.smallPadding
            )
            .background(
                color = color,
                shape = CircleShape
            )
            .size(size)
    )
}