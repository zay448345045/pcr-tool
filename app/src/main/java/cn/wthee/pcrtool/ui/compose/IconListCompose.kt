package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import coil.annotation.ExperimentalCoilApi

/**
 * 角色图标列表
 */
@ExperimentalCoilApi
@Composable
fun IconListCompose(icons: List<Int>, toCharacterDetail: (Int) -> Unit) {
    VerticalGrid(maxColumnWidth = Dimen.iconSize + Dimen.mediuPadding * 2) {
        icons.forEach {
            val unitId: Int
            val iconId: Int
            if (it / 10000 == 3) {
                //item 转 unit
                iconId = it % 10000 * 100 + 11
                unitId = it % 10000 * 100 + 1
            } else {
                iconId = it + 30
                unitId = it
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Dimen.mediuPadding,
                        end = Dimen.mediuPadding,
                        top = Dimen.mediuPadding
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconCompose(
                    data = Constants.UNIT_ICON_URL + iconId + Constants.WEBP
                ) {
                    toCharacterDetail(unitId)
                }
            }

        }
    }
}


@ExperimentalCoilApi
@Preview
@Composable
private fun IconListComposePreview() {
    val mockData = arrayListOf<Int>()
    for (i in 0..10) {
        mockData.add(i)
    }
    PreviewBox {
        IconListCompose(icons = mockData, toCharacterDetail = {})
    }
}