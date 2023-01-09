package com.example.universalcalendar.ui.feature.monthcalendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.universalcalendar.model.DayDto
import java.time.LocalDate

class MonthViewModel : ViewModel() {

    private val _currentDayDto: MutableLiveData<DayDto> = MutableLiveData()

    fun currentDayDto(): LiveData<DayDto> {
        return _currentDayDto
    }

    fun updateCurrentDayDto(date: LocalDate?) {
        val dateNow = date ?: LocalDate.now()
        _currentDayDto.value = DayDto(
            dayOfWeek = dateNow.dayOfWeek.toString(),
            day = dateNow.dayOfMonth.toString(),
            month = dateNow.monthValue.toString(),
            year = dateNow.year.toString()
        )
    }

}