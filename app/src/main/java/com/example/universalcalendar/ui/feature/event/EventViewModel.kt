package com.example.universalcalendar.ui.feature.event

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.universalcalendar.model.DayDto
import java.time.LocalDate

class EventViewModel : ViewModel() {
    private val _currentDayDto: MutableLiveData<DayDto> = MutableLiveData()

    fun currentDayDto(): LiveData<DayDto> {
        return _currentDayDto
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
}