package com.example.universalcalendar.ui.feature.event

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.databinding.FragmentEventBinding
import com.example.universalcalendar.ui.base.BaseFragment
import com.example.universalcalendar.widgets.DayViewContainer
import com.example.universalcalendar.widgets.WeekViewContainer
import com.kizitonwose.calendar.core.*
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import androidx.lifecycle.Observer
import com.example.universalcalendar.model.DayDto
import com.kizitonwose.calendar.view.WeekDayBinder
import com.kizitonwose.calendar.view.WeekHeaderFooterBinder


class EventFragment : BaseFragment<FragmentEventBinding, EventViewModel>() {

    private var selectedDate: LocalDate? = null
    private lateinit var currentDate: LocalDate
    private lateinit var startDate: LocalDate
    private lateinit var endDate: LocalDate

    override fun getViewModelClass(): Class<EventViewModel> = EventViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_event

    override fun initView() {
        binding.eventCalendar.dayBinder = object : WeekDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view).apply {
                onClickDate = {
                    binding.eventCalendar.notifyDateChanged(selectedDate ?: weekDay.date)
                    selectedDate = weekDay.date
                    binding.eventCalendar.notifyDateChanged(weekDay.date)
                    viewModel.updateCurrentDayDto(selectedDate)
                }
            }

            override fun bind(container: DayViewContainer, data: WeekDay) {
                container.weekDay = data
                container.textView.text = data.date.dayOfMonth.toString()
                container.textView.setTextColor(Color.BLACK)
                when (data.date) {
                    selectedDate -> container.textView.setBackgroundResource(R.drawable.bg_event_calendar_date_selected)
                    LocalDate.now() -> container.textView.setBackgroundResource(R.drawable.bg_event_calendar_date_now)
                    else -> container.textView.background = null
                }
            }
        }
        binding.eventCalendar.weekScrollListener = {
            updateWeekAfterScroll(it)
        }
    }

    override fun initData() {
        currentDate = LocalDate.now()
        selectedDate = LocalDate.now()
        viewModel.updateCurrentDayDto(selectedDate)
        startDate = currentDate.minusMonths(Constant.Calendar.NUMBER_ADD_WEEK_TO_CALENDAR)
        endDate = currentDate.plusMonths(Constant.Calendar.NUMBER_ADD_WEEK_TO_CALENDAR)
        val daysOfWeek = daysOfWeek()
        binding.eventCalendar.setup(startDate, endDate, daysOfWeek.first())
        binding.eventCalendar.scrollToWeek(currentDate)
        binding.eventCalendar.weekHeaderBinder = object :
            WeekHeaderFooterBinder<WeekViewContainer> {
            override fun create(view: View) = WeekViewContainer(view)
            override fun bind(container: WeekViewContainer, data: Week) {
                if (container.titlesContianer.tag == null) {
                    container.titlesContianer.tag = data
                    container.titlesContianer.children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val dayOfWeek = daysOfWeek()[index]
                            val title =
                                dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            textView.text = Constant.Calendar.MAP_DAY_WEEK_TITLE[title]
                        }
                }
            }
        }
        viewModel.currentDayDto().observe(viewLifecycleOwner, Observer {
            updateListEventByDate(it)
        })
    }

    private fun updateWeekAfterScroll(week: Week) {
        currentDate =
            week.days.firstOrNull { it.date == LocalDate.now() }?.date ?: week.days.first().date
        binding.eventCalendar.notifyDateChanged(selectedDate ?: currentDate)
        selectedDate = currentDate
        binding.eventCalendar.notifyDateChanged(currentDate)
        viewModel.updateCurrentDayDto(selectedDate)
        when (currentDate) {
            endDate -> addNextWeekToCalendar()
            startDate -> addLastWeekToCalendar()
            else -> {}
        }
    }

    private fun updateListEventByDate(dayDto: DayDto) {
        val monthHeaderTitle = "Tháng ${dayDto.month} - ${dayDto.year}"
        binding.eventCalendarHeaderTitle.text = monthHeaderTitle
        //update list event
    }

    private fun addNextWeekToCalendar() {
        val daysOfWeek = daysOfWeek()
        endDate = currentDate.plusMonths(Constant.Calendar.NUMBER_ADD_WEEK_TO_CALENDAR)
        binding.eventCalendar.setup(startDate, endDate, daysOfWeek.first())
        binding.eventCalendar.scrollToWeek(currentDate)
    }

    private fun addLastWeekToCalendar() {
        val daysOfWeek = daysOfWeek()
        startDate = currentDate.minusMonths(Constant.Calendar.NUMBER_ADD_WEEK_TO_CALENDAR)
        binding.eventCalendar.setup(startDate, endDate, daysOfWeek.first())
        binding.eventCalendar.scrollToWeek(currentDate)
    }


}