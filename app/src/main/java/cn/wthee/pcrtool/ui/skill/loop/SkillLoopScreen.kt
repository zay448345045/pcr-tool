package cn.wthee.pcrtool.ui.skill.loop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.AttackPattern
import cn.wthee.pcrtool.data.db.view.SkillBasicData
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.data.model.SkillLoop
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.VerticalGridList
import cn.wthee.pcrtool.ui.skill.getSkillColor
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper


/**
 * 技能循环
 * @param scrollable 是否可滚动，false：不可滚动，用于列表。true：可滚动，用于bottom sheet
 */
@Composable
fun SkillLoopScreen(
    attackPatternList: List<AttackPattern>,
    modifier: Modifier = Modifier,
    unitType: UnitType,
    scrollable: Boolean = false,
    skillLoopViewModel: SkillLoopViewModel = hiltViewModel()
) {
    val uiState by skillLoopViewModel.uiState.collectAsStateWithLifecycle()


    val loops = arrayListOf<SkillLoop>()
    val loopList = arrayListOf<Int>()
    var unitId = 0

    //处理技能循环数据
    attackPatternList.forEach { attackPattern ->
        if (attackPattern.getBefore().size > 0) {
            loopList.addAll(attackPattern.getBefore())
            loops.add(
                SkillLoop(
                    attackPattern.unitId,
                    attackPattern.patternId,
                    stringResource(R.string.before_loop),
                    attackPattern.getBefore()
                )
            )
        }
        if (attackPattern.getLoop().size > 0) {
            loopList.addAll(attackPattern.getLoop())
            loops.add(
                SkillLoop(
                    attackPattern.unitId,
                    attackPattern.patternId,
                    stringResource(R.string.looping),
                    attackPattern.getLoop()
                )
            )
        }
        unitId = attackPattern.unitId
    }

    LaunchedEffect(attackPatternList) {
        skillLoopViewModel.loadData(loopList, unitId, unitType)
    }


    Column(
        modifier = if (scrollable) {
            modifier.verticalScroll(rememberScrollState())
        } else {
            modifier
        }
    ) {

        if (loops.isNotEmpty()) {
            loops.forEach {
                SkillLoopItemContent(
                    loop = it,
                    skillMap = uiState.skillMap,
                    atkCastTime = uiState.atkCastTime
                )
            }
        }
        if (scrollable) {
            CommonSpacer()
        }
    }
}

/**
 * 技能循环 item
 */
@Composable
private fun SkillLoopItemContent(
    loop: SkillLoop,
    skillMap: HashMap<Int, SkillBasicData>,
    atkCastTime: Double
) {
    val loopList = loop.loopList.filter { it != 0 }

    //标题
    MainTitleText(text = loop.loopTitle)

    VerticalGridList(
        modifier = Modifier
            .padding(top = Dimen.mediumPadding),
        itemCount = loopList.size,
        itemWidth = Dimen.iconItemWidth,
        verticalContentPadding = Dimen.commonItemPadding,
        verticalAlignment = Alignment.Top
    ) {
        val skillId = loopList[it]
        val type: String
        val url: Any?
        val skillBasicData = skillMap[skillId]
        //准备时间
        val castTime: Double

        if (skillId == 1) {
            type = stringResource(id = R.string.normal_attack)
            url = R.drawable.unknown_item
            castTime = atkCastTime
        } else {
            type = when (skillId / 1000) {
                1 -> stringResource(id = R.string.skill_index, skillId % 10)
                2 -> "SP" + stringResource(id = R.string.skill_index, skillId % 10)
                else -> ""
            }

            url = if (skillBasicData == null) {
                R.drawable.unknown_item
            } else {
                ImageRequestHelper.getInstance()
                    .getUrl(ImageRequestHelper.ICON_SKILL, skillBasicData.iconType)
            }
            castTime = skillBasicData?.skillCastTime ?: 0.0
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainIcon(data = url)
            CaptionText(
                text = type,
                color = getSkillColor(type = type),
                modifier = Modifier.padding(top = Dimen.smallPadding),
                maxLines = 1,
                textAlign = TextAlign.Center
            )

            //准备时间
            CaptionText(
                text = stringResource(
                    id = R.string.cast_time,
                    castTime.toBigDecimal().stripTrailingZeros().toPlainString()
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}


@CombinedPreviews
@Composable
private fun SkillLoopItemContentPreview() {
    PreviewLayout {
        SkillLoopItemContent(
            loop = SkillLoop(
                unitId = 0,
                patternId = 1,
                loopTitle = stringResource(R.string.before_loop),
                loopList = arrayListOf(1, 1001, 1002, 1001, 1002, 1002, 1)
            ),
            skillMap = hashMapOf(),
            atkCastTime = 0.234567
        )
    }
}