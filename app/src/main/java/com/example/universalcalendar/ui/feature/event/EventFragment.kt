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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalcalendar.extensions.SharePreference
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.model.DayDto
import com.example.universalcalendar.ui.HomeActivity
import com.example.universalcalendar.ui.adapter.EventAdapter
import com.example.universalcalendar.ui.feature.monthcalendar.entities.EventDto
import com.kizitonwose.calendar.view.WeekDayBinder
import com.kizitonwose.calendar.view.WeekHeaderFooterBinder
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


class EventFragment : BaseFragment<FragmentEventBinding, EventViewModel>() {

    private var selectedDate: LocalDate? = null
    private lateinit var currentDate: LocalDate
    private lateinit var startDate: LocalDate
    private lateinit var endDate: LocalDate
    private var birthDayDate: LocalDate? = null
    private var listEvent: ArrayList<EventDto> = arrayListOf()
    private var adapter: EventAdapter? = null

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
                if (data.date == birthDayDate) {
                    container.showImageDob()
                }
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

    override fun initAction() {
        binding.icEventRegister.click {
            (activity as HomeActivity).navigateToEventSetupScreen()
        }
    }

    override fun initAdapter() {
        adapter = EventAdapter(listEvent)
        binding.rvListEvent.layoutManager = LinearLayoutManager(context)
        binding.rvListEvent.adapter = adapter
        viewModel.listEventData().observe(viewLifecycleOwner, Observer {
            updateListTitleByDate(it)
        })
        viewModel.currentDayDto().observe(viewLifecycleOwner, Observer {
            updateTitleByDate(it)
        })
        viewModel.currentEventPosition().observe(viewLifecycleOwner, Observer {
            if (it != null) (binding.rvListEvent.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                it,
                0
            )
            binding.rvListEvent.visibility = View.VISIBLE
        })
    }

    override fun initData() {
        binding.rvListEvent.visibility = View.INVISIBLE
        val userInfo = SharePreference.getInstance().getUserInformation()
        if (userInfo != null) {
            val dateOfBirth = userInfo.dateOfBirth ?: ""
            birthDayDate = LocalDate.parse("$dateOfBirth", DateTimeFormatter.BASIC_ISO_DATE)
        }
        currentDate = LocalDate.now()
        selectedDate = LocalDate.now()
        getDataEvents()
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
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getDataEvents() {
        GlobalScope.launch(Dispatchers.Unconfined) {
            viewModel.updateCurrentDayDto(selectedDate)
            viewModel.fetchDataEvent(context)
        }
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

    private fun updateTitleByDate(dayDto: DayDto) {
        val monthHeaderTitle = "Tháng ${dayDto.month} - ${dayDto.year}"
        binding.eventCalendarHeaderTitle.text = monthHeaderTitle
        viewModel.updateCurrentEventPosition()
    }

    private fun updateListTitleByDate(events: ArrayList<EventDto>) {
        listEvent.clear()
        listEvent.addAll(events)
        adapter?.refreshData(listEvent)
        viewModel.updateCurrentEventPosition()
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