package com.example.universalcalendar.ui.dialog

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.common.Strings
import com.example.universalcalendar.extensions.DateUtils
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.ui.base.BaseDialog
import kotlinx.android.synthetic.main.date_picker_dialog.view.datePicker
import kotlinx.android.synthetic.main.date_picker_dialog.view.ic_check_date
import kotlinx.android.synthetic.main.date_picker_dialog.view.ic_close_date_picker
import kotlinx.android.synthetic.main.date_picker_dialog.view.tv_date_picker_day_of_week
import kotlinx.android.synthetic.main.date_picker_dialog.view.tv_date_picker_type
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class DatePickerDialog : BaseDialog() {

    companion object {
        fun newInstance() = DatePickerDialog()
        private val TYPE_PICKER = arrayOf("Dương", "Âm")
        private const val TYPE_PICKER_AL = "Âm"
        private const val TYPE_PICKER_DL = "Dương"
    }

    private lateinit var mView: View
    private var typePickStatus = TYPE_PICKER_DL
    var datePickerCallback: (date: LocalDate) -> Unit = {}
    private var currentDay = 0
    private var currentMonth = 0
    private var currentYear = 0


    override fun createCustomView(): View {
        mView = View.inflate(context, R.layout.date_picker_dialog, null)
        initView()
        return mView
    }

    private fun initView() {
        val today = Calendar.getInstance()
        currentDay = today.get(Calendar.DAY_OF_MONTH)
        currentMonth = today.get(Calendar.MONTH) + 1
        currentYear = today.get(Calendar.YEAR)
        updateDayOfWeek()
        mView.datePicker?.init(
            currentYear, currentMonth - 1, currentDay
        ) { _, year, month, day ->
            currentDay = day
            currentMonth = month + 1
            currentYear = year
            updateDayOfWeek()
        }
        mView.ic_close_date_picker?.click { dismissDialog() }
        mView.ic_check_date?.click {
            val month = if ("$currentMonth".length < 2) "0$currentMonth" else "$currentMonth"
            val day = if ("$currentDay".length < 2) "0$currentDay" else "$currentDay"
            val dateStr = DateUtils.convertDateToString(
                DateUtils.convertStringToDate(
                    DateUtils.DATE_LOCALE_FORMAT_2,
                    "$currentYear$month$day"
                ), DateUtils.DATE_LOCALE_FORMAT_2
            ) ?: Strings.EMPTY
            val date = LocalDate.parse(dateStr, DateTimeFormatter.BASIC_ISO_DATE)
            datePickerCallback.invoke(date)
            dismissDialog()
        }
        val typePicker = mView.tv_date_picker_type
        typePicker?.minValue = 0
        typePicker?.maxValue = 1
        typePicker?.displayedValues = TYPE_PICKER
        typePicker?.setOnValueChangedListener { _, _, newVal ->
            typePickStatus = TYPE_PICKER[newVal]
            changeTypePicker()
        }
    }

    private fun updateDayOfWeek() {
        val month = if ("$currentMonth".length < 2) "0$currentMonth" else "$currentMonth"
        val day = if ("$currentDay".length < 2) "0$currentDay" else "$currentDay"
        val dayOfWeek = DateUtils.convertDateToString(
            DateUtils.convertStringToDate(
                DateUtils.DATE_LOCALE_FORMAT_2,
                "$currentYear$month$day"
            ), DateUtils.WEEK_DAY_FORMAT
        ) ?: Strings.EMPTY
        mView.tv_date_picker_day_of_week?.text =
            Constant.Calendar.MAP_DAY_WEEK_TITLE[dayOfWeek.uppercase(
                Locale.getDefault()
            )] ?: Strings.EMPTY
    }

    private fun changeTypePicker() {
        //
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
        show(fm, DatePickerDialog::class.java.name)
        return this
    }

    interface DatePickDialogListener {
        fun onUpdateDateAfterPick(date: LocalDate)
    }
}