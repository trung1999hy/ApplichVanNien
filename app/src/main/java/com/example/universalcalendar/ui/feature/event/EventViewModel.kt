package com.example.universalcalendar.ui.feature.event

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.extensions.SharePreference
import com.example.universalcalendar.model.DayDto
import com.example.universalcalendar.model.Event
import com.example.universalcalendar.ui.adapter.EventAdapter
import com.example.universalcalendar.ui.feature.monthcalendar.entities.EventDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.time.LocalDate

class EventViewModel : ViewModel() {
    private val _currentDayDto: MutableLiveData<DayDto> = MutableLiveData()
    private val mListEventDto: ArrayList<Event> = arrayListOf()
    private val mListEventUserRegisterDto: ArrayList<Event> = arrayListOf()
    private val _currentEventPosition: MutableLiveData<Int> = MutableLiveData()
    private val _mListCurrentEventDto: MutableLiveData<ArrayList<EventDto>> = MutableLiveData()

    fun currentDayDto(): LiveData<DayDto> {
        return _currentDayDto
    }

    fun currentEventPosition(): LiveData<Int> {
        return _currentEventPosition
    }

    fun listEventData(): LiveData<ArrayList<EventDto>> {
        return _mListCurrentEventDto
    }

    fun updateCurrentDayDto(date: LocalDate?) {
        val dateNow = date ?: LocalDate.now()
        _currentDayDto.value = DayDto(
            dayOfWeek = dateNow.dayOfWeek.toString(),
            day = dateNow.dayOfMonth,
            month = dateNow.monthValue,
            year = dateNow.year
        )
    }

    fun fetchDataEvent(context: Context?) {
        mListEventDto.clear()
        mListEventUserRegisterDto.clear()
        lateinit var jsonStringEvent: String
        val listEventRegister = SharePreference.getInstance().getEventRegister()
        try {
            jsonStringEvent = context?.assets?.open(Constant.PATH_DATA_EVENT)
                ?.bufferedReader()
                .use { it?.readText() ?: "{}" }
        } catch (ioException: IOException) {
            //
        }

        val listEventType = object : TypeToken<List<Event>>() {}.type
        val listEvent: List<Event> = Gson().fromJson(jsonStringEvent, listEventType)
        mListEventDto.addAll(listEvent)
        if (!listEventRegister.isNullOrEmpty()) {
            mListEventUserRegisterDto.addAll(listEventRegister)
        }
        updateListCurrentEvent()
    }

    private fun updateListCurrentEvent() {
        if (mListEventDto.isEmpty() && mListEventUserRegisterDto.isEmpty()) return
        val listEventByDate = mListEventDto.filter { it.yearSolar != 0 }
            .sortedWith(compareBy<Event> { it.yearSolar }
                .thenBy { it.monthSolar }
                .thenBy { it.daySolar })

        val listEventEveryYear = mListEventDto.filter { it.yearSolar == 0 }
            .sortedWith(compareBy<Event> { it.monthSolar }
                .thenBy { it.daySolar })

        val listEventRegister =
            mListEventUserRegisterDto.sortedWith(compareBy<Event> { it.yearSolar }
                .thenBy { it.monthSolar }
                .thenBy { it.daySolar }
                .thenBy { it.timeStart }
                .thenBy { it.timeEnd })

        var listEventDto = arrayListOf<EventDto>()
        listEventRegister.forEach { eventRegister ->
                listEventDto.add(
                    EventDto(
                        viewType = EventAdapter.ItemViewType.EVENT_DETAIL,
                        dayOfWeek = eventRegister.daySolar.toString(),
                        day = eventRegister.daySolar.toString(),
                        month = eventRegister.monthSolar.toString(),
                        year = eventRegister.yearSolar.toString(),
                        timeStart = eventRegister.timeStart,
                        timeEnd = eventRegister.timeEnd,
                        contentEvent = eventRegister.title,
                        address = eventRegister.address,
                        isHightLight = true
                    )
                )
            }
        listEventByDate.forEach {
            val eventOfYear =
                listEventEveryYear.firstOrNull { eventYear ->
                    it.daySolar == eventYear.daySolar &&
                            it.monthSolar == eventYear.monthSolar && it.yearSolar == eventYear.yearSolar
                }?.title ?: ""
            listEventDto.add(
                EventDto(
                    viewType = EventAdapter.ItemViewType.EVENT_DETAIL,
                    dayOfWeek = it.daySolar.toString(),
                    day = it.daySolar.toString(),
                    month = it.monthSolar.toString(),
                    year = it.yearSolar.toString(),
                    contentEvent = if (eventOfYear.isNotEmpty()) "${it.title}\n$eventOfYear" else it.title,
                    address = it.address
                )
            )
        }
        val listEventSorted = arrayListOf<EventDto>()
        listEventSorted.addAll(listEventDto.sortedWith(compareBy<EventDto> { it.year }
            .thenBy { it.month }
            .thenBy { it.day }
            .thenBy { it.timeStart }
            .thenBy { it.timeEnd }))
        _mListCurrentEventDto.value = listEventSorted
    }

    fun updateCurrentEventPosition() {
        if (_mListCurrentEventDto.value.isNullOrEmpty()) return
        val day = _currentDayDto.value?.day ?: LocalDate.now().dayOfMonth
        val month = _currentDayDto.value?.month ?: LocalDate.now().monthValue
        val year = _currentDayDto.value?.year ?: LocalDate.now().year
        val positionEventByDay =
            _mListCurrentEventDto.value?.indexOfFirst { it.day == day.toString() && it.month == month.toString() && it.year == year.toString() }
        val positionEventByMonth =
            _mListCurrentEventDto.value?.indexOfFirst { it.year == year.toString() && it.month == month.toString() }
        val positionEventByYear =
            _mListCurrentEventDto.value?.indexOfFirst { it.year == year.toString() }
        _currentEventPosition.postValue(
            if (positionEventByDay != -1) positionEventByDay
            else if (positionEventByMonth != -1) positionEventByMonth
            else positionEventByYear
        )
    }

}