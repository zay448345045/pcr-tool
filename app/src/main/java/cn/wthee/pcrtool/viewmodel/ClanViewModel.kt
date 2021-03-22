package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.ClanRepository
import cn.wthee.pcrtool.data.entity.EnemyParameter
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.database.DatabaseUpdater
import kotlinx.coroutines.launch

/**
 * 活动 ViewModel
 *
 * 数据来源 [ClanRepository]
 */
class ClanViewModel(
    private val repository: ClanRepository
) : ViewModel() {

    var clanInfo = MutableLiveData<List<ClanBattleInfo>>()
    var clanBossAttr = MutableLiveData<EnemyParameter>()


    /**
     * 获取团队战记录
     */
    fun getAllClanBattleData() {
        viewModelScope.launch {
            val data = repository.getAllClanBattleData(DatabaseUpdater.getDatabaseType())
            clanInfo.postValue(data)
        }
    }

    /**
     * 获取 BOSS 属性
     */
    fun getBossAttr(enemyId: Int) {
        viewModelScope.launch {
            val data = repository.getBossAttr(enemyId)
            clanBossAttr.postValue(data)
        }
    }

}
