package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.RankColor
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.getFormatText


/**
 * 蓝底白字
 */
@Composable
fun MainTitleText(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    maxLines: Int = Int.MAX_VALUE,
    selectable: Boolean = false
) {
    val content: @Composable () -> Unit = {
        Text(
            text = text,
            color = colorWhite,
            style = textStyle,
            maxLines = maxLines,
            modifier = (if (selectable) Modifier else modifier)
                .background(color = backgroundColor, shape = MaterialTheme.shapes.extraSmall)
                .padding(start = Dimen.mediumPadding, end = Dimen.mediumPadding),
        )
    }

    if (selectable) {
        SelectionContainer(modifier = modifier, content = content)
    } else {
        content()
    }
}

/**
 * 内容文本
 */
@Composable
fun MainContentText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.End,
    selectable: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    val content: @Composable () -> Unit = {
        Text(
            text = text,
            textAlign = textAlign,
            color = color,
            style = MaterialTheme.typography.bodyLarge,
            modifier = if (selectable) Modifier else modifier,
            overflow = TextOverflow.Ellipsis,
            maxLines = maxLines
        )
    }

    if (selectable) {
        //fixme 2024-06-04 添加一层 Surface 。直接使用 SelectionContainer 时，设置 weight 无效
        Surface(modifier = modifier) {
            SelectionContainer(content = content)
        }
    } else {
        content()
    }
}

/**
 * 蓝色加粗标题
 */
@Composable
fun MainText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Center,
    color: Color = MaterialTheme.colorScheme.primary,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    selectable: Boolean = false,
) {
    val content: @Composable () -> Unit = {
        Text(
            text = text,
            color = color,
            style = style,
            textAlign = textAlign,
            fontWeight = FontWeight.Black,
            modifier = if (selectable) Modifier else modifier
        )
    }

    if (selectable) {
        SelectionContainer(modifier = modifier, content = content)
    } else {
        content()
    }
}

/**
 * 副标题
 */
@Composable
fun Subtitle1(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    selectable: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign = TextAlign.Start,
) {
    val content: @Composable () -> Unit = {
        Text(
            text = text,
            color = color,
            textAlign = textAlign,
            style = MaterialTheme.typography.titleMedium,
            modifier = if (selectable) Modifier else modifier,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }

    if (selectable) {
        SelectionContainer(modifier = modifier, content = content)
    } else {
        content()
    }
}

/**
 * 副标题
 */
@Composable
fun Subtitle2(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    selectable: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign = TextAlign.Start,
    fontWeight: FontWeight = FontWeight.Normal
) {
    val content: @Composable () -> Unit = {
        Text(
            text = text,
            color = color,
            textAlign = textAlign,
            style = MaterialTheme.typography.titleSmall,
            modifier = if (selectable) Modifier else modifier,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            fontWeight = fontWeight
        )
    }
    if (selectable) {
        SelectionContainer(modifier = modifier, content = content)
    } else {
        content()
    }
}

/**
 * 灰色标注字体
 */
@Composable
fun CaptionText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.End,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = MaterialTheme.typography.bodySmall,
) {
    Text(
        text = text,
        textAlign = textAlign,
        color = color,
        style = style,
        modifier = modifier,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}


/**
 * RANK 文本
 * type: 0 默认 1 白字+底色
 */
@Composable
fun RankText(
    modifier: Modifier = Modifier,
    rank: Int,
    textAlign: TextAlign = TextAlign.Center,
    type: Int = 0
) {
    val color = RankColor.getRankColor(rank)
    val text = getFormatText(rank)
    if (type == 0) {
        Text(
            modifier = modifier,
            text = text,
            textAlign = textAlign,
            color = color,
            style = MaterialTheme.typography.titleSmall
        )
    } else {
        Text(
            text = text,
            color = colorWhite,
            style = MaterialTheme.typography.titleSmall,
            modifier = modifier
                .background(color = color, shape = MaterialTheme.shapes.extraSmall)
                .padding(horizontal = Dimen.smallPadding)
        )
    }
}

/**
 * 选中文本
 * @param selected 是否选中
 * @param selectedColor 选中的颜色
 */
@Composable
fun SelectText(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    selected: Boolean,
    text: String,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    padding: Dp = Dimen.smallPadding,
    margin: Dp = Dimen.smallPadding,
    textAlign: TextAlign = TextAlign.Center
) {
    val context = LocalContext.current
    val mModifier = if (selected) {
        modifier
            .padding(top = margin)
            .background(color = selectedColor, shape = MaterialTheme.shapes.extraSmall)
            .padding(start = padding, end = padding)
    } else {
        (if (onClick != null) {
            modifier
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable {
                    VibrateUtil(context).single()
                    onClick()
                }
        } else {
            modifier
        })
            .padding(top = margin, start = padding, end = padding)
    }
    Text(
        text = text,
        color = if (selected) colorWhite else textColor,
        style = textStyle,
        maxLines = 1,
        textAlign = textAlign,
        overflow = TextOverflow.Ellipsis,
        modifier = mModifier
    )
}

/**
 * 头部标题
 */
@Composable
fun HeaderText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
    )
}


/**
 * 居中文本
 */
@Composable
fun CenterTipText(text: String, content: (@Composable () -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(min = Dimen.cardHeight),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //内容
        MainText(
            text = text,
            modifier = Modifier.padding(Dimen.mediumPadding),
            selectable = true
        )
        //额外内容
        if (content != null) {
            content()
        }
    }
}

/**
 * 通用标题内容组件，用例：角色属性
 */
@Composable
fun CommonTitleContentText(modifier: Modifier = Modifier, title: String, content: String) {
    Row(
        modifier = modifier.padding(
            top = Dimen.smallPadding,
            start = Dimen.commonItemPadding,
            end = Dimen.commonItemPadding
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainTitleText(
            text = title,
            modifier = Modifier
                .weight(0.3f),
            maxLines = 1
        )
        MainContentText(
            text = content,
            modifier = Modifier.weight(0.2f)
        )
    }
}

/**
 * 通用分组标题
 */
@Composable
fun CommonGroupTitle(
    modifier: Modifier = Modifier,
    iconData: Any? = null,
    titleStart: String,
    titleCenter: String = "",
    titleEnd: String,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = colorWhite,
    iconSize: Dp = Dimen.iconSize
) {
    val startPadding = if (iconData == null) {
        0.dp
    } else {
        Dimen.smallPadding
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconData?.let {
            MainIcon(
                data = iconData,
                size = iconSize
            )
        }
        Box(
            modifier = Modifier
                .padding(start = startPadding)
                .weight(1f)
                .background(
                    color = backgroundColor,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(horizontal = Dimen.mediumPadding)
        ) {
            Row {
                Subtitle2(
                    text = titleStart,
                    color = textColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Subtitle2(
                    text = titleEnd,
                    color = textColor
                )
            }
            Subtitle2(
                text = titleCenter,
                color = textColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }

    }
}


@CombinedPreviews
@Composable
private fun AllTextPreview() {
    val text = stringResource(id = R.string.debug_short_text)
    PreviewLayout {
        MainTitleText(text = "MainTitleText $text")
        MainContentText(text = "MainContentText $text")
        MainText(text = "MainText $text")
        Subtitle1(text = "Subtitle1 $text")
        Subtitle2(text = "Subtitle2 $text")
        CaptionText(text = "CaptionText $text")
        HeaderText(text = "HeaderText $text")
        RankText(rank = 21)
        SelectText(text = "SelectText $text", selected = true)
        CommonTitleContentText(title = "Title $text", content = "Content $text")
    }
}
