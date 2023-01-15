package com.example.universalcalendar.ui.dialog

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.FragmentManager
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.ui.base.BaseDialog
import com.example.universalcalendar.widgets.DayViewContainer
import com.example.universalcalendar.widgets.MonthViewContainer
import com.kizitonwose.calendar.core.*
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import kotlinx.android.synthetic.main.dialog_time_event_register.view.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*


class TimeEventRegisterDialog : BaseDialog() {

    companion object {
        fun newInstance() = TimeEventRegisterDialog()
        const val TYPE_SETUP_START = 1
        const val TYPE_SETUP_END = 2
    }

    private lateinit var mView: View
    var timeSetupCallback: ((timeStart: LocalDateTime, timeEnd: LocalDateTime) -> Unit)? = null
    var timeEndSetupCallback: ((timeEnd: LocalDateTime) -> Unit)? = null
    var typeSetUp = TYPE_SETUP_START
    lateinit var timeTarget: LocalDateTime
    lateinit var timeCurrent: LocalDateTime
    lateinit var dateCurrent: LocalDate
    private lateinit var monthCurrent: YearMonth
    private lateinit var startMonth: YearMonth
    private lateinit var endMonth: YearMonth

    override fun createCustomView(): View {
        mView = View.inflate(context, R.layout.dialog_time_event_register, null)
        initView()
        return mView
    }

    private fun initView() {
        initViewCalendar()
        initTimePicker()
        mView.ic_close_time_picker?.click { dismissDialog() }
        mView.ic_check_time_valid?.click { checkTimeValid() }
    }
    
    private fun initViewCalendar() {
        monthCurrent = YearMonth.from(dateCurrent)
        startMonth =
            if (monthCurrent >= YearMonth.now()) monthCurrent.minusMonths(Constant.Calendar.NUMBER_ADD_MONTH_TO_CALENDAR)
            else monthCurrent
        endMonth = monthCurrent.plusMonths(Constant.Calendar.NUMBER_ADD_MONTH_TO_CALENDAR)
        mView.event_register_month_calendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view).apply {
                onClickDate = {
                    if (day.position == DayPosition.MonthDate && dateCurrent != day.date && day.date >= LocalDate.now()) {
                        mView.event_register_month_calendar.notifyDateChanged(dateCurrent)
                        dateCurrent = day.date
                        mView.event_register_month_calendar.notifyDateChanged(day.date)
                        updateTitleYearMonthTitle()
                    } else if (day.date >= LocalDate.now()) {
                        dateCurrent = day.date
                        mView.event_register_month_calendar.notifyDateChanged(day.date)
                        mView.event_register_month_calendar.smoothScrollToMonth(day.date.yearMonth)
                        updateTitleYearMonthTitle()
                    } else {
                        Toast.makeText(context, "Không thể chọn ngày đã qua !", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()
                if (data.position == DayPosition.MonthDate && data.date >= LocalDate.now()) {
                    container.textView.setTextColor(Color.BLACK)
                    when (data.date) {
                        dateCurrent -> container.textView.setBackgroundResource(R.drawable.bg_month_calendar_date_selected)
                        LocalDate.now() -> container.textView.setBackgroundResource(R.drawable.bg_month_calendar_date_now)
                        else -> container.textView.background = null
                    }
                } else {
                    container.textView.setTextColor(Color.GRAY)
                    container.textView.background = null
                }
            }
        }
        mView.event_register_month_calendar.monthHeaderBinder = object :
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
        mView.event_register_month_calendar.monthScrollListener = {
            updateMonthAfterScroll(it.yearMonth)
        }
        val daysOfWeek = daysOfWeek()
        mView.event_register_month_calendar.setup(startMonth, endMonth, daysOfWeek.first())
        mView.event_register_month_calendar.scrollToMonth(monthCurrent)
    }

    private fun initTimePicker() {
        mView.event_register_time_setup?.setOnTimeChangedListener { _, hour, minute ->
            val hourTime = if (hour < 10) "0$hour" else hour.toString()
            val minuteTime = if (minute < 10) "0$minute" else minute.toString()
            val monthTime = if (dateCurrent.monthValue < 10) "0${dateCurrent.monthValue}" else dateCurrent.monthValue.toString()
            val dayTime = if (dateCurrent.dayOfMonth < 10) "0${dateCurrent.dayOfMonth}" else dateCurrent.dayOfMonth.toString()
            timeCurrent = LocalDateTime.parse("${dateCurrent.year}$monthTime$dayTime$hourTime$minuteTime", DateTimeFormatter.ofPattern("yyyyMMddHHmm"))
            if (typeSetUp == TYPE_SETUP_START && timeCurrent > timeTarget) {
                val minuteTimeTarget = if (minute < 30) 30 - minute else 60 - minute
                timeTarget = timeCurrent.plusMinutes(minuteTimeTarget.toLong())
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mView.event_register_time_setup?.currentHour = timeCurrent.hour
            mView.event_register_time_setup?.currentMinute = timeCurrent.minute
        } else {
            mView.event_register_time_setup?.hour = timeCurrent.hour
            mView.event_register_time_setup?.minute = timeCurrent.minute
        }
    }

    private fun updateMonthAfterScroll(monthCalendar: YearMonth) {
        if (monthCalendar < YearMonth.now()) return
        monthCurrent = monthCalendar
        if (dateCurrent.yearMonth != monthCurrent) {
            val datemonthCurrentSelect = if (monthCurrent == YearMonth.now()) LocalDate.now()
            else monthCurrent.atDay(1)
            mView.event_register_month_calendar?.notifyDateChanged(dateCurrent)
            dateCurrent = datemonthCurrentSelect
            mView.event_register_month_calendar?.notifyDateChanged(datemonthCurrentSelect)
            updateTitleYearMonthTitle()
        }
        when (monthCurrent) {
            endMonth -> addNextMonthToCalendar()
            startMonth -> addLastMonthToCalendar()
            else -> {}
        }
    }

    private fun updateTitleYearMonthTitle() {
        val yearMonth = "Tháng ${dateCurrent.monthValue}, ${dateCurrent.year}"
        mView.tv_event_register_month_year_title?.text = yearMonth
    }

    private fun addNextMonthToCalendar(number: Long = Constant.Calendar.NUMBER_ADD_MONTH_TO_CALENDAR) {
        val daysOfWeek = daysOfWeek()
        endMonth = monthCurrent.plusMonths(number)
        mView.event_register_month_calendar?.updateMonthData(startMonth, endMonth, daysOfWeek.first())
        mView.event_register_month_calendar?.scrollToMonth(monthCurrent)
    }

    private fun addLastMonthToCalendar(number: Long = Constant.Calendar.NUMBER_ADD_MONTH_TO_CALENDAR) {
        val daysOfWeek = daysOfWeek()
        startMonth = monthCurrent.minusMonths(number)
        mView.event_register_month_calendar?.updateMonthData(startMonth, endMonth, daysOfWeek.first())
        mView.event_register_month_calendar?.scrollToMonth(monthCurrent)
    }

    private fun checkTimeValid() {
        if (typeSetUp == TYPE_SETUP_END) {
            if (timeCurrent <= timeTarget) {
                Toast.makeText(context, "Không thể chọn thời gian bé hơn thời gian bắt đầu", Toast.LENGTH_SHORT).show()
            } else {
                timeEndSetupCallback?.invoke(timeCurrent)
                dismissDialog()
            }
        } else {
            timeSetupCallback?.invoke(timeCurrent, timeTarget)
            dismissDialog()
        }
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun shows(fm: FragmentManager): BaseDialog {
        show(fm, TimeEventRegisterDialog::class.java.name)
        return this
    }

}