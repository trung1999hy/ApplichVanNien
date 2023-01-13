package com.example.universalcalendar.ui.feature.monthcalendar

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.universalcalendar.model.DayDto
import com.example.universalcalendar.model.Event
import com.example.universalcalendar.ui.adapter.EventAdapter
import com.example.universalcalendar.ui.feature.monthcalendar.entities.EventDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.time.LocalDate

class MonthViewModel : ViewModel() {

    companion object {
        private const val PATH_DATA_EVENT = "event.json"
    }

    private val _currentDayDto: MutableLiveData<DayDto> = MutableLiveData()
    private val mListEventDto: ArrayList<Event> = arrayListOf()
    private val _mListCurrentEventDto: MutableLiveData<ArrayList<EventDto>> = MutableLiveData()

    fun currentDayDto(): LiveData<DayDto> {
        return _currentDayDto
    }

    fun currentEventDto(): LiveData<ArrayList<EventDto>> {
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
        updateListCurrentEvent()
    }

    fun fetchDataEvent(context: Context?) {
        mListEventDto.clear()
        _mListCurrentEventDto.value?.clear()
        lateinit var jsonString: String
        try {
            jsonString = context?.assets?.open(PATH_DATA_EVENT)
                ?.bufferedReader()
                .use { it?.readText() ?: "{}" }
        } catch (ioException: IOException) {
            //
        }

        val listCountryType = object : TypeToken<List<Event>>() {}.type
        val listEvent: List<Event> = Gson().fromJson(jsonString, listCountryType)
        mListEventDto.addAll(listEvent)
        updateListCurrentEvent()
    }

    private fun updateListCurrentEvent() {
        val listEventByDate = mListEventDto.filter { event ->
            event.daySolar == _currentDayDto.value?.day &&
                    event.monthSolar == _currentDayDto.value?.month &&
                    event.yearSolar == _currentDayDto.value?.year
        }
        val listEventDto = arrayListOf<EventDto>()
        listEventByDate.forEach {
            listEventDto.add(
                EventDto(
                    viewType = EventAdapter.ItemViewType.EVENT_DAY,
                    dayOfWeek = it.daySolar.toString(),
                    day = it.daySolar.toString(),
                    month = it.monthSolar.toString(),
                    year = it.yearSolar.toString(),
                    contentEvent = it.title
                )
            )
        }
        _mListCurrentEventDto.value = listEventDto
    }

}