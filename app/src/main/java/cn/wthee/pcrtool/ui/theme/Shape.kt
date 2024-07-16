package cn.wthee.pcrtool.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import kotlin.math.sqrt


/**
 * 左上、右上圆角
 */
@Composable
fun shapeTop() = RoundedCornerShape(
    topStart = MaterialTheme.shapes.medium.topStart,
    topEnd = MaterialTheme.shapes.medium.topEnd,
    bottomStart = CornerSize(0),
    bottomEnd = CornerSize(0)
)

/**
 * 矩形（没有圆角）
 */
@Composable
fun noShape() = CutCornerShape(0.dp)

/**
 * 比例
 */
const val RATIO_GOLDEN = 0.618f

/**
 * 梯形
 */
val TrapezoidShape = GenericShape { size, _ ->
    moveTo(size.width * (1 - RATIO_GOLDEN), 0f)
    lineTo(0f, size.height)
    lineTo(size.width, size.height)
    lineTo(size.width, 0f)
}

/**
 * 倒三角
 */
val invertedTriangleShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width / 2, size.height * sqrt(3f) / 2)
    lineTo(0f, 0f)
}