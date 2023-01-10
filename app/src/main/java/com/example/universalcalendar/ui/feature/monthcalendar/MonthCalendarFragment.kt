package com.example.universalcalendar.ui.feature.monthcalendar

import android.graphics.Color
import android.view.View
import android.widget.TextView
import androidx.core.view.children
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Strings
import com.example.universalcalendar.databinding.FragmentMonthCalendarBinding
import com.example.universalcalendar.model.DayDto
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
import androidx.lifecycle.Observer
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.ui.dialog.DatePickerDialog
import java.time.temporal.ChronoUnit

class MonthCalendarFragment : BaseFragment<FragmentMonthCalendarBinding, MonthViewModel>() {

    private var selectedDate: LocalDate? = null
    private lateinit var currentMonth: YearMonth
    private lateinit var startMonth: YearMonth
    private lateinit var endMonth: YearMonth

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
                        viewModel.updateCurrentDayDto(selectedDate)
                    } else {
                        selectedDate = day.date
                        binding.monthCalendar.notifyDateChanged(day.date)
                        binding.monthCalendar.smoothScrollToMonth(day.date.yearMonth)
                        viewModel.updateCurrentDayDto(selectedDate)
                    }
                }
            }

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.BLACK)
                    when (data.date) {
                        selectedDate -> container.textView.setBackgroundResource(R.drawable.bg_month_calendar_date_selected)
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
        initAction()
    }

    private fun initAction() {
        binding.llChooseMonth.setOnClickListener {
            val dialogDatePicker = DatePickerDialog.newInstance()
            dialogDatePicker.apply {
                datePickerCallback = {
                    currentMonth = YearMonth.from(it)
                    when {
                        currentMonth > endMonth -> addNextMonthToCalendar(
                            ChronoUnit.MONTHS.between(endMonth, currentMonth)
                        )
                        currentMonth < startMonth -> addLastMonthToCalendar(
                            ChronoUnit.MONTHS.between(currentMonth, startMonth)
                        )
                    }
                    binding.monthCalendar.notifyDateChanged(selectedDate ?: it)
                    selectedDate = it
                    binding.monthCalendar.notifyDateChanged(it)
                    viewModel.updateCurrentDayDto(selectedDate)
                    binding.monthCalendar.smoothScrollToMonth(currentMonth)
                }
            }
            dialogDatePicker.shows(childFragmentManager)
        }
    }

    override fun initData() {
        binding.monthCalendarViewModel = viewModel
        currentMonth = YearMonth.now()
        selectedDate = LocalDate.now()
        viewModel.updateCurrentDayDto(selectedDate)
        startMonth = currentMonth.minusMonths(Constant.Calendar.NUMBER_ADD_MONTH_TO_CALENDAR)
        endMonth = currentMonth.plusMonths(Constant.Calendar.NUMBER_ADD_MONTH_TO_CALENDAR)
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
                            textView.text = Constant.Calendar.MAP_DAY_WEEK_TITLE[title]
                        }
                }
            }
        }
        viewModel.currentDayDto().observe(viewLifecycleOwner, Observer {
            updateTitleCurrentDate(it)
        })
    }

    private fun updateMonthAfterScroll(monthCalendar: YearMonth) {
        currentMonth = monthCalendar
        if (selectedDate?.yearMonth != currentMonth) {
            val dateCurrentMonthSelect = if (currentMonth == YearMonth.now()) LocalDate.now()
            else currentMonth.atDay(1)
            binding.monthCalendar.notifyDateChanged(selectedDate ?: dateCurrentMonthSelect)
            selectedDate = dateCurrentMonthSelect
            binding.monthCalendar.notifyDateChanged(dateCurrentMonthSelect)
            viewModel.updateCurrentDayDto(selectedDate)
        }
        when (currentMonth) {
            endMonth -> addNextMonthToCalendar()
            startMonth -> addLastMonthToCalendar()
            else -> {}
        }
    }

    private fun updateTitleCurrentDate(dayDto: DayDto) {
        val dayOfWeekTitle = Constant.Calendar.MAP_DAY_WEEK_TITLE[dayDto.dayOfWeek?.uppercase(Locale.getDefault())] ?: Strings.EMPTY
        val monthContentTimeTitle = "${dayDto.day} Tháng ${dayDto.month}, ${dayDto.year}"
        val monthTitleHeader = "$dayOfWeekTitle, $monthContentTimeTitle"
        binding.tvMonthSelectTitle.text = monthTitleHeader
        binding.monthDateDetailTitle.text =  dayOfWeekTitle
        binding.monthDateDetailPositive.text = monthContentTimeTitle
    }

    private fun addNextMonthToCalendar(number: Long = Constant.Calendar.NUMBER_ADD_MONTH_TO_CALENDAR) {
        val daysOfWeek = daysOfWeek()
        endMonth = currentMonth.plusMonths(number)
        binding.monthCalendar.updateMonthData(startMonth, endMonth, daysOfWeek.first())
        binding.monthCalendar.scrollToMonth(currentMonth)
    }

    private fun addLastMonthToCalendar(number: Long = Constant.Calendar.NUMBER_ADD_MONTH_TO_CALENDAR) {
        val daysOfWeek = daysOfWeek()
        startMonth = currentMonth.minusMonths(number)
        binding.monthCalendar.updateMonthData(startMonth, endMonth, daysOfWeek.first())
        binding.monthCalendar.scrollToMonth(currentMonth)
    }
}