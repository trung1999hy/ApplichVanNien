package com.example.universalcalendar.ui.feature.monthcalendar

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.extensions.DateUtils
import com.example.universalcalendar.extensions.SharePreference
import com.example.universalcalendar.model.DayDto
import com.example.universalcalendar.model.Event
import com.example.universalcalendar.model.HourGood
import com.example.universalcalendar.ui.adapter.EventAdapter
import com.example.universalcalendar.ui.feature.monthcalendar.entities.EventDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.internal.toImmutableList
import java.io.IOException
import java.time.LocalDate

class MonthViewModel : ViewModel() {

    private val _currentDayDto: MutableLiveData<DayDto> = MutableLiveData()
    private val mListEventDto: ArrayList<Event> = arrayListOf()
    private val _mListCurrentEventDto: MutableLiveData<ArrayList<EventDto>> = MutableLiveData()
    private val mListEventUserRegisterDto: ArrayList<Event> = arrayListOf()
    private val mListZodiac: MutableLiveData<ArrayList<HourGood>> = MutableLiveData()

    fun currentDayDto(): LiveData<DayDto> {
        return _currentDayDto
    }

    fun currentEventDto(): LiveData<ArrayList<EventDto>> {
        return _mListCurrentEventDto
    }

    fun currentZodiac(): LiveData<ArrayList<HourGood>> {
        return mListZodiac
    }

    fun updateCurrentDayDto(date: LocalDate?) {
        val dateNow = date ?: LocalDate.now()
        _currentDayDto.postValue(DayDto(
            dayOfWeek = dateNow.dayOfWeek.toString(),
            day = dateNow.dayOfMonth,
            month = dateNow.monthValue,
            year = dateNow.year
        ))
    }

    fun fetchDataEvent(context: Context?) {
        mListEventDto.clear()
        _mListCurrentEventDto.value?.clear()
        mListEventUserRegisterDto.clear()
        lateinit var jsonString: String
        val listEventRegister = SharePreference.getInstance().getEventRegister()
        try {
            jsonString = context?.assets?.open(Constant.PATH_DATA_EVENT)
                ?.bufferedReader()
                .use { it?.readText() ?: "{}" }
        } catch (ioException: IOException) {
            //
        }

        val listCountryType = object : TypeToken<List<Event>>() {}.type
        val listEvent: List<Event> = Gson().fromJson(jsonString, listCountryType)
        mListEventDto.addAll(listEvent)
        if (!listEventRegister.isNullOrEmpty()) {
            mListEventUserRegisterDto.addAll(listEventRegister)
        }
        updateListCurrentEvent()
    }

    fun updateListCurrentEvent() {
        if (mListEventDto.isEmpty()) return
        val listEventByDate = mListEventDto.filter { event ->
            event.monthSolar == _currentDayDto.value?.month &&
                    event.yearSolar == _currentDayDto.value?.year && event.yearSolar != 0
        }.sortedBy { it.daySolar }

        val listEventEveryYear = mListEventDto.filter { it.yearSolar == 0 }
            .sortedWith(compareBy<Event> { it.monthSolar }
                .thenBy { it.daySolar })

        val listEventRegister = mListEventUserRegisterDto.sortedWith(compareBy<Event> { it.yearSolar }
            .thenBy { it.monthSolar }
            .thenBy { it.daySolar }
            .thenBy { it.timeStart }
            .thenBy { it.timeEnd })

        val listEventDto = arrayListOf<EventDto>()
        listEventRegister.forEach { eventRegister ->
            listEventDto.add(
                EventDto(
                    viewType = EventAdapter.ItemViewType.EVENT_DAY,
                    dayOfWeek = eventRegister.daySolar.toString(),
                    day = eventRegister.daySolar.toString(),
                    month = eventRegister.monthSolar.toString(),
                    year = eventRegister.yearSolar.toString(),
                    timeStart = eventRegister.timeStart,
                    timeEnd = eventRegister.timeEnd,
                    contentEvent = eventRegister.title,
                    address = eventRegister.address
                )
            )
        }
        listEventByDate.forEach {
            val eventOfYear =
                listEventEveryYear.firstOrNull { eventYear ->
                    it.daySolar == eventYear.daySolar &&
                            it.monthSolar == eventYear.monthSolar }?.title ?: ""
            listEventDto.add(
                EventDto(
                    viewType = EventAdapter.ItemViewType.EVENT_DAY,
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
        listEventSorted.addAll(listEventDto.sortedWith(compareBy<EventDto> { it.year?.toInt() }
            .thenBy { it.month?.toInt() }
            .thenBy { it.day?.toInt() }
            .thenBy { it.timeStart }
            .thenBy { it.timeEnd }))
        _mListCurrentEventDto.postValue(listEventSorted)
    }

    fun updateListZodiac() {
        _currentDayDto.value?.let {
            val list = DateUtils.getListGioHD(it.day, it.month, it.year)
            mListZodiac.postValue(list)
        }
    }

}