package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.EventRepository
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.enums.EventType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 首页纵览
 */
@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    private val equipmentRepository: EquipmentRepository,
    private val eventRepository: EventRepository,
    private val gachaRepository: GachaRepository,
    private val apiRepository: MyAPIRepository
) : ViewModel() {
    val newOverview = MutableLiveData<ResponseData<List<NewsTable>>>()

    /**
     * 获取角色数量
     */
    fun getCharacterCount() = flow {
        try {
            emit(unitRepository.getCount())
        } catch (_: Exception) {

        }
    }

    /**
     * 获取角色列表
     */
    fun getCharacterInfoList() = flow {
        try {
            val filterList = unitRepository.getCharacterInfoList(FilterCharacter(), 50)
            emit(filterList.subList(0, 10))
        } catch (_: Exception) {

        }
    }

    /**
     * 获取装备数量
     */
    fun getEquipCount() = flow {
        try {
            emit(equipmentRepository.getCount())
        } catch (_: Exception) {

        }
    }

    /**
     * 获取装备列表
     */
    fun getEquipList(limit: Int) = flow {
        try {
            emit(equipmentRepository.getEquipments(FilterEquipment(), limit))
        } catch (_: Exception) {

        }
    }

    /**
     * 获取卡池列表
     */
    fun getGachaList(type: EventType) = flow {
        try {
            val today = getToday()
            //数据库时间格式问题，导致查询不出最新的，先多查询一些数据
            val data = gachaRepository.getGachaHistory(200)

            if (type == EventType.IN_PROGRESS) {
                emit(
                    data.filter {
                        isInProgress(today, it.startTime, it.endTime)
                    }.sortedWith(compareGacha(today))
                )
            } else {
                emit(
                    data.filter {
                        isComingSoon(today, it.startTime)
                    }.sortedWith(compareGacha(today))
                )
            }
        } catch (_: Exception) {

        }
    }

    /**
     * 获取活动列表
     */
    fun getCalendarEventList(type: EventType) = flow {
        try {
            val today = getToday()
            val data =
                eventRepository.getDropEvent() + eventRepository.getTowerEvent(1) + eventRepository.getSpDungeonEvent(
                    1
                )

            if (type == EventType.IN_PROGRESS) {
                emit(
                    data.filter {
                        isInProgress(today, it.startTime, it.endTime)
                    }.sortedWith(compareEvent(today))
                )
            } else {
                emit(
                    data.filter {
                        isComingSoon(today, it.startTime)
                    }.sortedWith(compareEvent(today))
                )
            }
        } catch (_: Exception) {

        }

    }

    /**
     * 获取剧情活动列表
     */
    fun getStoryEventList(type: EventType) = flow {
        try {
            val today = getToday()
            val data = eventRepository.getAllEvents(10)

            if (type == EventType.IN_PROGRESS) {
                emit(
                    data.filter {
                        isInProgress(today, it.startTime, it.endTime)
                    }.sortedWith(compareStoryEvent(today))
                )
            } else {
                emit(
                    data.filter {
                        isComingSoon(today, it.startTime)
                    }.sortedWith(compareStoryEvent(today))
                )
            }
        } catch (_: Exception) {

        }
    }

    /**
     * 获取新闻
     */
    fun getNewsOverview() {
        viewModelScope.launch {
            try {
                val data = apiRepository.getNewsOverviewByRegion(MainActivity.regionType)
                newOverview.postValue(data)
            } catch (_: Exception) {

            }
        }
    }

    /**
     * 六星 id 列表
     */
    fun getR6Ids() {
        viewModelScope.launch {
            try {
                navViewModel.r6Ids.postValue(unitRepository.getR6Ids())
            } catch (_: Exception) {

            }
        }
    }

    /**
     * 获取免费十连卡池列表
     */
    fun getFreeGachaList(type: EventType) = flow {
        try {
            val today = getToday()
            val data = eventRepository.getFreeGachaEvent(1)

            if (type == EventType.IN_PROGRESS) {
                emit(data.filter {
                    isInProgress(today, it.startTime, it.endTime)
                })
            } else {
                emit(data.filter {
                    isComingSoon(today, it.startTime)
                })
            }
        } catch (_: Exception) {

        }
    }

    /**
     * 获取生日日程列表
     */
    fun getBirthdayList(type: EventType) = flow {
        try {
            val today = getToday()
            val data = eventRepository.getBirthdayList().sortedWith(compareBirthDay())

            if (type == EventType.IN_PROGRESS) {
                var list = data.filter {
                    isInProgress(today, it.getStartTime(), it.getEndTime())
                }
                //只取一条记录
                if (list.isNotEmpty()) {
                    list = list.subList(0, 1)
                }
                emit(list)
            } else {
                var list = data.filter {
                    isComingSoon(today, it.getStartTime())
                }
                //只取一条记录
                if (list.isNotEmpty()) {
                    list = list.subList(0, 1)
                }
                emit(list)
            }
        } catch (_: Exception) {

        }
    }

    /**
     * 获取最新公会战日程
     */
    fun getClanBattleEvent(type: EventType) = flow {
        try {
            val today = getToday()
            var data = eventRepository.getClanBattleEvent(1)
            //测试用
            if (BuildConfig.DEBUG) {
                data = eventRepository.getClanBattleEvent(2)
                data[0].startTime = calcDate(today, 5, false)
                data[0].endTime = calcDate(today, 10, false)
                data[1].startTime = calcDate(today, 2, true)
                data[1].endTime = calcDate(today, 3, false)
            }

            if (type == EventType.IN_PROGRESS) {
                emit(data.filter {
                    isInProgress(today, it.startTime, it.getFixedEndTime())
                })
            } else {
                emit(data.filter {
                    isComingSoon(today, it.startTime)
                })
            }
        } catch (_: Exception) {

        }
    }
}