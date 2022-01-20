package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 活动 ViewModel
 *
 * @param eventRepository
 */
@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    /**
     * 获取活动记录
     */
    fun getEventHistory() = flow {
        try {
            emit(eventRepository.getAllEvents(Int.MAX_VALUE))
        } catch (e: Exception) {

        }
    }
}
