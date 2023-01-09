package com.example.universalcalendar.ui.feature.monthcalendar

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Strings
import com.example.universalcalendar.databinding.FragmentMonthCalendarBinding
import com.example.universalcalendar.extensions.DateUtils
import com.example.universalcalendar.ui.base.BaseFragment
import com.example.universalcalendar.widgets.DayViewContainer
import com.example.universalcalendar.widgets.MonthViewContainer
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

class MonthCalendarFragment : BaseFragment<FragmentMonthCalendarBinding, MonthViewModel>() {

    companion object {
        private val NUMBER_ADD_MONTH_TO_CALENDAR = 1L
    }

    private var selectedDate: LocalDate? = null
    private lateinit var  currentMonth: YearMonth
    private lateinit var  startMonth: YearMonth
    private lateinit var  endMonth: YearMonth
    private val mapDayWeekTitle : MutableMap<String,String> = mutableMapOf(
        "MONDAY" to "Thứ Hai",
        "TUESDAY" to "Thứ Ba",
        "WEDNESDAY" to "Thứ Tư",
        "THURSDAY" to "Thứ Năm",
        "FRIDAY" to "Thứ Sáu",
        "SATURDAY" to "Thứ Bảy",
        "SUNDAY" to "Chủ Nhật",
        "Mon" to "Hai",
        "Tue" to "Ba",
        "Wed" to "Tư",
        "Thu" to "Năm",
        "Fri" to "Sáu",
        "Sat" to "Bảy",
        "Sun" to "C.n",
    )

    override fun getViewModelClass(): Class<MonthViewModel> = MonthViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_month_calendar

    override fun initView() {
        binding.monthCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view).apply {
                onClickDate = {
                    if (day.position == DayPosition.MonthDate && selectedDate != day.date) {
                        binding.monthCalendar.notifyDateChanged(selectedDate ?: day.date)
                        selectedDate = day.date
                        binding.monthCalendar.notifyDateChanged(day.date)
                        updateTitleCurrentDate()
                    } else {
                        selectedDate = day.date
                        binding.monthCalendar.notifyDateChanged(day.date)
                        binding.monthCalendar.smoothScrollToMonth(day.date.yearMonth)
                        updateTitleCurrentDate()
                    }
                }
            }

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.BLACK)
                    when (data.date) {
                        selectedDate ->  container.textView.setBackgroundResource(R.drawable.bg_month_calendar_date_selected)
                        LocalDate.now() -> container.textView.setBackgroundResource(R.drawable.bg_month_calendar_date_now)
                        else -> container.textView.background = null
                    }
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }
            }
        }
        binding.monthCalendar.monthScrollListener = {
            updateMonthAfterScroll(it.yearMonth)
        }
    }

    override fun initData() {
        currentMonth = YearMonth.now()
        selectedDate = LocalDate.now()
        updateTitleCurrentDate()
        startMonth = currentMonth.minusMonths(NUMBER_ADD_MONTH_TO_CALENDAR)
        endMonth = currentMonth.plusMonths(NUMBER_ADD_MONTH_TO_CALENDAR)
        val daysOfWeek = daysOfWeek()
        binding.monthCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.monthCalendar.scrollToMonth(currentMonth)
        binding.monthCalendar.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                if (container.titlesContianer.tag == null) {
                    container.titlesContianer.tag = data.yearMonth
                    container.titlesContianer.children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val dayOfWeek = daysOfWeek()[index]
                            val title =
                                dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            textView.text = mapDayWeekTitle[title]
                        }
                }
            }
        }
    }

    private fun updateMonthAfterScroll(monthCalendar: YearMonth) {
        currentMonth = monthCalendar
        if (selectedDate?.yearMonth != currentMonth) {
            val dateCurrentMonthSelect = if (currentMonth == YearMonth.now()) LocalDate.now()
            else currentMonth.atDay(1)
            binding.monthCalendar.notifyDateChanged(selectedDate ?: dateCurrentMonthSelect)
            selectedDate = dateCurrentMonthSelect
            binding.monthCalendar.notifyDateChanged(dateCurrentMonthSelect)
            updateTitleCurrentDate()
        }
        when (currentMonth) {
            endMonth -> addNextMonthToCalendar()
            startMonth -> addLastMonthToCalendar()
            else -> {}
        }
    }

    private fun updateTitleCurrentDate() {
        val weekDay = mapDayWeekTitle[selectedDate?.dayOfWeek.toString()]  ?: Strings.EMPTY
        val day = selectedDate?.dayOfMonth ?: Strings.EMPTY
        val month = selectedDate?.monthValue ?: Strings.EMPTY
        val year = selectedDate?.year ?: Strings.EMPTY
        val monthContentTimeTitle = "$day Tháng $month, $year"
        val monthTitleHeader = "$weekDay, $monthContentTimeTitle"

        binding.tvMonthSelectTitle.text = monthTitleHeader
        binding.monthDateDetailTitle.text = weekDay.uppercase(Locale.getDefault())
        binding.monthDateDetailPositive.text = monthContentTimeTitle
    }

    private fun addNextMonthToCalendar() {
        val daysOfWeek = daysOfWeek()
        endMonth = currentMonth.plusMonths(NUMBER_ADD_MONTH_TO_CALENDAR)
        binding.monthCalendar.updateMonthData(startMonth, endMonth, daysOfWeek.first())
        binding.monthCalendar.scrollToMonth(currentMonth)
    }

    private fun addLastMonthToCalendar() {
        val daysOfWeek = daysOfWeek()
        startMonth = currentMonth.minusMonths(NUMBER_ADD_MONTH_TO_CALENDAR)
        binding.monthCalendar.updateMonthData(startMonth, endMonth, daysOfWeek.first())
        binding.monthCalendar.scrollToMonth(currentMonth)
    }

}