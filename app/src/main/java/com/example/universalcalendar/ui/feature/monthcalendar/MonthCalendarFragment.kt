package com.example.universalcalendar.ui.feature.monthcalendar

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.LinearLayout
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.extensions.DateUtils
import com.example.universalcalendar.extensions.SharePreference
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.model.Event
import com.example.universalcalendar.model.HourGood
import com.example.universalcalendar.ui.HomeActivity
import com.example.universalcalendar.ui.adapter.EventAdapter
import com.example.universalcalendar.ui.adapter.ZodiacAdapter
import com.example.universalcalendar.ui.dialog.DatePickerDialog
import com.example.universalcalendar.ui.feature.monthcalendar.entities.EventDto
import kotlinx.android.synthetic.main.fragment_day_calendar.view.*
import kotlinx.coroutines.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.collections.ArrayList

class MonthCalendarFragment : BaseFragment<FragmentMonthCalendarBinding, MonthViewModel>() {

    private var selectedDate: LocalDate? = null
    private var birthDayDate: LocalDate? = null
    private lateinit var currentMonth: YearMonth
    private lateinit var startMonth: YearMonth
    private lateinit var endMonth: YearMonth
    private var listEvent: ArrayList<EventDto> = arrayListOf()
    private var adapter: EventAdapter? = null
    private var listZodiac: ArrayList<HourGood> = arrayListOf()
    private var adapterZodiac: ZodiacAdapter? = null

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
                if (data.date == birthDayDate) {
                    container.showImageDob()
                }
                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.BLACK)
                    when (data.date) {
                        selectedDate -> container.textView.setBackgroundResource(R.drawable.bg_month_calendar_date_selected)
                        LocalDate.now() -> container.textView.setBackgroundResource(R.drawable.bg_month_calendar_date_now)
                        else -> container.textView.background = null
                    }
                } else {
                    container.textView.setTextColor(Color.GRAY)
                    container.textView.background = null
                }
            }
        }
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
        binding.monthCalendar.monthScrollListener = {
            updateMonthAfterScroll(it.yearMonth)
        }
    }

    override fun initAction() {
        binding.llChooseMonth.click {
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
        binding.icMonthAddEvent.click {
            (activity as HomeActivity).navigateToEventSetupScreen()
        }
    }

    override fun initAdapter() {
        binding.monthCalendarViewModel = viewModel
        adapter = EventAdapter(listEvent)
        binding.rvMonthCalendarEvents.layoutManager = LinearLayoutManager(context)
        binding.rvMonthCalendarEvents.adapter = adapter
        viewModel.currentEventDto().observe(viewLifecycleOwner, Observer {
            updateListEventCurrentDate(it)
        })
        adapterZodiac = ZodiacAdapter(listZodiac)
        binding.rvMonthCalendarHourZodiac.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMonthCalendarHourZodiac.adapter = adapterZodiac
        viewModel.currentEventDto().observe(viewLifecycleOwner, Observer {
            updateListEventCurrentDate(it)
        })
        viewModel.currentDayDto().observe(viewLifecycleOwner, Observer {
            updateTitleCurrentDate(it)
        })
        viewModel.currentZodiac().observe(viewLifecycleOwner, Observer {
            updateHourZodiac(it)
        })
    }

    override fun initData() {
        val userInfo = SharePreference.getInstance().getUserInformation()
        if (userInfo != null) {
            val dateOfBirth = userInfo.dateOfBirth ?: ""
            birthDayDate = LocalDate.parse("$dateOfBirth", DateTimeFormatter.BASIC_ISO_DATE)
        }
        currentMonth = YearMonth.now()
        selectedDate = LocalDate.now()
        getDataEvents()
        startMonth = currentMonth.minusMonths(Constant.Calendar.NUMBER_ADD_MONTH_TO_CALENDAR)
        endMonth = currentMonth.plusMonths(Constant.Calendar.NUMBER_ADD_MONTH_TO_CALENDAR)
        val daysOfWeek = daysOfWeek()
        binding.monthCalendar.setup(startMonth, endMonth, daysOfWeek.first())
        binding.monthCalendar.scrollToMonth(currentMonth)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getDataEvents() {
        GlobalScope.launch(Dispatchers.Unconfined) {
            viewModel.updateCurrentDayDto(selectedDate)
            viewModel.fetchDataEvent(context)
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
            viewModel.updateCurrentDayDto(selectedDate)
        }
        when (currentMonth) {
            endMonth -> addNextMonthToCalendar()
            startMonth -> addLastMonthToCalendar()
            else -> {}
        }
    }

    private fun updateTitleCurrentDate(dayDto: DayDto) {
        val lunarDay = DateUtils.convertSolar2Lunar(dayDto.day, dayDto.month, dayDto.year, 7.00)
        val dayOfWeekTitle = Constant.Calendar.MAP_DAY_WEEK_TITLE[dayDto.dayOfWeek?.uppercase(Locale.getDefault())] ?: Strings.EMPTY
        val monthContentTimeTitle = "${dayDto.day} Tháng ${dayDto.month}, ${dayDto.year}"
        val monthTitleHeader = "$dayOfWeekTitle, $monthContentTimeTitle"
        val monthLunar = DateUtils.getCanChiForMonth(lunarDay[1], lunarDay[2])
        val yearLunar = "Năm ${DateUtils.getCanYearLunar(lunarDay[2])} ${DateUtils.getChiYearLunar(lunarDay[2])}"
        val dayLunar = "${lunarDay[0]} $monthLunar, $yearLunar"
        binding.tvMonthSelectTitle.text = monthTitleHeader
        binding.monthDateDetailTitle.text =  dayOfWeekTitle
        binding.monthDateDetailPositive.text = monthContentTimeTitle
        binding.monthDayDetailLunar.text = dayLunar
        binding.tvDayNameLunar.text = DateUtils.getCanChiDayLunar(dayDto.day, dayDto.month, dayDto.year)
        binding.tvMonthNameLunar.text = monthLunar
        binding.tvYearNameLunar.text = yearLunar
        viewModel.updateListZodiac()
    }

    private fun updateListEventCurrentDate(events: ArrayList<EventDto>) {
        if (events.isNotEmpty()) {
            listEvent.clear()
            binding.tvMonthListEventEmpty.visibility = View.GONE
            binding.rvMonthCalendarEvents.visibility = View.VISIBLE
            listEvent.addAll(events)
            adapter?.refreshData(listEvent)
        } else {
            binding.rvMonthCalendarEvents.visibility = View.GONE
            binding.tvMonthListEventEmpty.visibility = View.VISIBLE
        }

    }

    private fun updateHourZodiac(list: ArrayList<HourGood>) {
        listZodiac = list
        adapterZodiac?.refreshData(listZodiac)
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