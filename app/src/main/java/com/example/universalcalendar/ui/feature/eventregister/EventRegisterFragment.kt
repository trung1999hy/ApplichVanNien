package com.example.universalcalendar.ui.feature.eventregister

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.databinding.FragmentEventRegisterBinding
import com.example.universalcalendar.extensions.AlarmUtils
import com.example.universalcalendar.extensions.SharePreference
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.model.Event
import com.example.universalcalendar.ui.HomeActivity
import com.example.universalcalendar.ui.base.BaseFragment
import com.example.universalcalendar.ui.dialog.TimeEventRegisterDialog
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


class EventRegisterFragment : BaseFragment<FragmentEventRegisterBinding, EventRegisterViewModel>() {

    companion object {
        private const val TIME_ADD_30_MINUTES = 30
        private const val TIME_ADD_60_MINUTES = 60
        const val TYPE_REGISTER_EVENT = "register"
        const val TYPE_UPDATE_EVENT = "update"
    }

    private var currentDate : LocalDate = LocalDate.now()
    private lateinit var startDate : LocalDateTime
    private lateinit var endDate : LocalDateTime

    override fun getViewModelClass(): Class<EventRegisterViewModel> =
        EventRegisterViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_event_register

    override fun initView() {
        binding.etEventRegisterJobTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvEventRegisterError.visibility = View.GONE
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.etEventRegisterJobContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.tvEventRegisterError.visibility = View.GONE
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })
        binding.scEventRegisterSetFullDay.setOnCheckedChangeListener { _, b ->
            updateTimeSetup()
        }
    }

    override fun initAction() {
        binding.icEventRegisterBack.click { onBackPress() }
        binding.icEventRegisterAccept.click { saveEvent() }
        binding.tvEventRegisterTimeStart.click {
            val timeSetupDialog = TimeEventRegisterDialog.newInstance()
            timeSetupDialog.apply {
                typeSetUp = TimeEventRegisterDialog.TYPE_SETUP_START
                timeTarget = endDate
                timeCurrent = startDate
                dateCurrent = currentDate
                timeSetupCallback = { start, end ->
                    startDate = start
                    endDate = end
                    updateTimeSetup()
                }
            }
            timeSetupDialog.shows(childFragmentManager)
        }
        binding.tvEventRegisterTimeEnd.click {
            val timeSetupDialog = TimeEventRegisterDialog.newInstance()
            timeSetupDialog.apply {
                typeSetUp = TimeEventRegisterDialog.TYPE_SETUP_END
                timeTarget = startDate
                timeCurrent = endDate
                dateCurrent = currentDate
                timeEndSetupCallback = {
                    endDate = it
                    updateTimeSetup()
                }
            }
            timeSetupDialog.shows(childFragmentManager)
        }
    }

    private fun saveEvent() {
        if (binding.etEventRegisterJobTitle.text?.isEmpty() == true) {
            binding.tvEventRegisterError.visibility = View.VISIBLE
            binding.tvEventRegisterError.text = "Tiêu đề không được bỏ trống"
        } else if(binding.etEventRegisterJobContent.text?.isEmpty() == true) {
            binding.tvEventRegisterError.visibility = View.VISIBLE
            binding.tvEventRegisterError.text = "Nội dung không được bỏ trống"
        } else {
            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm")
            val startTime = startDate.format(formatter)
            val endTime = endDate.format(formatter)
            val event = Event(
                id = (endTime.toLong()-startTime.toLong()).toInt(),
                daySolar = startDate.dayOfMonth,
                monthSolar = startDate.monthValue,
                yearSolar = startDate.year,
                timeStart = startTime,
                timeEnd = endTime,
                topic = binding.etEventRegisterJobTitle.text.toString(),
                title = binding.etEventRegisterJobContent.text.toString(),
                address = binding.etEventRegisterJobLocation.text.toString()
            )
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, startDate.year)
            calendar.set(Calendar.MONTH, startDate.monthValue - 1)
            calendar.set(Calendar.DAY_OF_MONTH, startDate.dayOfMonth)
            calendar.set(Calendar.MINUTE, startDate.minute)
            calendar.set(Calendar.HOUR_OF_DAY, startDate.hour)
            calendar.set(Calendar.SECOND, 0)
            SharePreference.getInstance().saveEvent(event)
            AlarmUtils.create(context, calendar, event)
            Toast.makeText(context, "Đăng kí sự kiện thành công !", Toast.LENGTH_SHORT).show()
            onBackPress()
        }
    }

    override fun initData() {
        val localDateNow = LocalDateTime.now()
        val minute = localDateNow.minute
        startDate =
            localDateNow.plusMinutes((if (minute < 30) 30 - minute else 60 - minute).toLong())
        endDate = startDate.plusMinutes(30L)
        updateTimeSetup()
    }

    private fun updateTimeSetup() {
        if (binding.scEventRegisterSetFullDay.isChecked) {
            val dayOfWeekStart =
                Constant.Calendar.MAP_DAY_WEEK_TITLE_2[startDate.dayOfWeek.toString()]
            val startTime =
                "$dayOfWeekStart, ${startDate.dayOfMonth}/${startDate.monthValue}/${startDate.year}\nCả ngày"
            val dayOfWeekEnd = Constant.Calendar.MAP_DAY_WEEK_TITLE_2[endDate.dayOfWeek.toString()]
            val endTime =
                "$dayOfWeekEnd, ${endDate.dayOfMonth}/${endDate.monthValue}/${endDate.year}\nCả ngày"
            binding.tvEventRegisterTimeStart.text = startTime
            binding.tvEventRegisterTimeEnd.text = endTime
        } else {
            val dayOfWeekStart =
                Constant.Calendar.MAP_DAY_WEEK_TITLE_2[startDate.dayOfWeek.toString()]
            val minuteStart = if (startDate.minute >= 10) startDate.minute.toString() else "0${startDate.minute}"
            val startTime =
                "$dayOfWeekStart, ${startDate.dayOfMonth}/${startDate.monthValue}/${startDate.year}\n${startDate.hour}:$minuteStart"
            val dayOfWeekEnd = Constant.Calendar.MAP_DAY_WEEK_TITLE_2[endDate.dayOfWeek.toString()]
            val minuteEnd = if (endDate.minute >= 10) endDate.minute.toString() else "0${endDate.minute}"
            val endTime =
                "$dayOfWeekEnd, ${endDate.dayOfMonth}/${endDate.monthValue}/${endDate.year}\n${endDate.hour}:$minuteEnd"
            binding.tvEventRegisterTimeStart.text = startTime
            binding.tvEventRegisterTimeEnd.text = endTime
        }
    }

    private fun onBackPress() {
        (activity as HomeActivity).onBackPressed()
    }

    override fun handleBack() {
        binding.etEventRegisterJobLocation.clearFocus()
    }

}