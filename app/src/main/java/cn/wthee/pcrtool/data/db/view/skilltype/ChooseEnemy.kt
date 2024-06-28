package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget

// 7：指定攻击对象
fun SkillActionDetail.chooseEnemy() = getString(R.string.skill_action_type_desc_7, getTarget())