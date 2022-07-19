package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.data.enums.UnitType
import cn.wthee.pcrtool.ui.skill.SkillLoopList
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.CharacterAttrViewModel
import cn.wthee.pcrtool.viewmodel.SkillViewModel

/**
 * 角色技能循环
 */
@Composable
fun CharacterSkillLoop(
    unitId: Int,
    skillViewModel: SkillViewModel = hiltViewModel(),
    attrViewModel: CharacterAttrViewModel = hiltViewModel()
) {
    //角色特殊六星id
    val cutinId = attrViewModel.getCutinId(unitId).collectAsState(initial = 0).value
    //技能循环
    val loopData =
        skillViewModel.getCharacterSkillLoops(unitId).collectAsState(initial = arrayListOf()).value
    val iconTypes = skillViewModel.getskillIconTypes(unitId, cutinId)
        .collectAsState(initial = hashMapOf()).value

    SkillLoopList(
        loopData,
        iconTypes,
        modifier = Modifier.padding(Dimen.largePadding),
        unitType = UnitType.CHARACTER
    )
}