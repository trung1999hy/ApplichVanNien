package com.example.universalcalendar.ui.feature.daycalendar

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.content.ContextCompat
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.databinding.FragmentDayCalendarBinding
import com.example.universalcalendar.extensions.*
import com.example.universalcalendar.model.Quotation
import com.example.universalcalendar.model.Quote
import com.example.universalcalendar.ui.base.BaseFragment
import com.example.universalcalendar.ui.dialog.DatePickerDialog
import com.example.universalcalendar.widgets.OnSwipeTouchListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_day_calendar.*
import java.util.*
import kotlin.collections.ArrayList

class DayCalendarFragment : BaseFragment<FragmentDayCalendarBinding, DayViewModel>(),
    DatePickerDialog.DatePickDialogListener {
    companion object {
        const val DAY_LUNAR = "day_lunar"
        const val MONTH_LUNAR = "month_lunar"
        const val YEAR_LUNAR = "year_lunar"
        const val DAY_SOLAR = "day_solar"
        const val MONTH_SOLAR = "month_solar"
        const val YEAR_SOLAR = "year_solar"
    }

    private var daySolar = 0
    private var monthSolar = 0
    private var yearSolar = 0
    private val mListQuotation = ArrayList<Quotation>()
    private val datePickerDialog = DatePickerDialog.newInstance()

    private lateinit var calendar: Calendar
    override fun getViewModelClass(): Class<DayViewModel> = DayViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_day_calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = Calendar.getInstance()
    }

    override fun initView() {
        datePickerDialog.listener = this
        daySolar = calendar.get(5)
        monthSolar = calendar.get(2) + 1
        yearSolar = calendar.get(1)
        loadDataCalendar(daySolar, monthSolar, yearSolar)
        getListQuoteFromAsset()
        setQuote()
        setImageBackground()
        loadDataWhenSwipe()
        binding.linearLayout.setOnClickListener {
            datePickerDialog.shows(childFragmentManager)
        }
        binding.frameCurrentDate.setOnClickListener {
            loadDataCalendar(
                calendar.get(5),
                calendar.get(2) + 1,
                calendar.get(1)
            )
        }
    }

    private fun getListQuoteFromAsset() {
        mListQuotation.clear()
        val type = object : TypeToken<List<Quote>>() {}.type
        val jsonFromAssets = Utils.getDataFromAsset(requireContext(), Constant.QUOTE) ?: ""
        val listQuotation = Gson().fromJson<List<Quote>>(jsonFromAssets, type)
        if (jsonFromAssets.isNotEmpty() && listQuotation != null && listQuotation.isNotEmpty()) {
            mListQuotation.addAll(listQuotation[Constant.INDEX_DEFAULT].list_dn as List<Quotation>)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setImageBackground() {
        if (binding.imageBackground != null) {
            try {
                when (Random().nextInt(5)) {
                    0 -> {
                        binding.imageBackground.setImageDrawable(resources.getDrawable(R.drawable.bg_lich0))
                    }

                    1 -> {
                        binding.imageBackground.setImageDrawable(resources.getDrawable(R.drawable.bg_lich1))
                    }

                    2 -> {
                        binding.imageBackground.setImageDrawable(resources.getDrawable(R.drawable.bg_lich2))
                    }

                    3 -> {
                        binding.imageBackground.setImageDrawable(resources.getDrawable(R.drawable.bg_lich3))
                    }

                    4 -> {
                        binding.imageBackground.setImageDrawable(resources.getDrawable(R.drawable.bg_lich4))
                    }

                    else -> {
                        binding.imageBackground.setImageDrawable(resources.getDrawable(R.drawable.bg_lich0))
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            }
        }
    }

    private fun setQuote() {
        val randomQuote = Random().nextInt(mListQuotation.size)
        binding.tvQuote.text = mListQuotation[randomQuote].quote
        binding.tvAuthor.text = mListQuotation[randomQuote].author
    }

    @SuppressLint("SetTextI18n")
    private fun loadDataCalendar(daySolar: Int, monthSolar: Int, yearSolar: Int) {
        val lunarDay = DateUtils.convertSolar2Lunar(daySolar, monthSolar, yearSolar, 7.00)
        binding.tvHour.formatDateTime(calendar.time)
        binding.tvDaySolar.text = if (daySolar < 10) "0${daySolar}" else "$daySolar"
        binding.tvDayLunar.text = lunarDay[0].toString()
        binding.tvMonthLunar.text = "Tháng ${lunarDay[1]}"
        //calculate canchiday using solar date
        binding.dayCanChi.text =
            "Ngày ${DateUtils.getCanChiDayLunar(daySolar, monthSolar, yearSolar)}"
        binding.monthCanChi.text = "Tháng ${DateUtils.getCanChiForMonth(lunarDay[1], lunarDay[2])}"
        binding.yearCanChi.text =
            "Năm ${DateUtils.getCanYearLunar(lunarDay[2])} ${DateUtils.getChiYearLunar(lunarDay[2])}"
        binding.dayOfWeek.text = DateUtils.getWeek(yearSolar, monthSolar, daySolar)
        if (binding.dayOfWeek.text == Constant.Calendar.MAP_DAY_WEEK_TITLE["SUNDAY"]) {
            binding.tvDaySolar.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            binding.tvDayLunar.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
            binding.tvMonthLunar.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        } else {
            binding.tvDaySolar.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blue_normal
                )
            )
            binding.tvDayLunar.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blue_normal
                )
            )
            binding.tvMonthLunar.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.blue_normal
                )
            )
        }
        updateTitleCurrentDate(monthSolar, yearSolar)
        if (isCurrentDate(daySolar, monthSolar, yearSolar)) binding.frameCurrentDate.visibility =
            View.GONE
        binding.frameCurrentDate.visibility = View.VISIBLE
        val isGoodDay = DateUtils.isGoodDay(
            DateUtils.getChiDayLunar(daySolar, monthSolar, yearSolar),
            lunarDay[1]
        )

        val isBadDay = DateUtils.isBadDay(
            DateUtils.getChiDayLunar(daySolar, monthSolar, yearSolar),
            lunarDay[1]
        )

        if (isGoodDay) {
            binding.tvStatusDay.text = resources.getString(R.string.tv_hoang_dao)
            binding.tvStatusDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        } else if (isBadDay) {
            binding.tvStatusDay.text = resources.getString(R.string.tv_hac_dao)
            binding.tvStatusDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        } else {
            binding.tvStatusDay.text = Constant.EMPTY
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadDataWhenSwipe() {
        binding.imageBackground.setOnTouchListener(object : OnSwipeTouchListener(activity) {
            override fun onSwipeRight() {
                loadDataCalendar(daySolar, monthSolar, yearSolar)
                calendar.add(5, -1)
                calendar.get(5).also { daySolar = it }
                calendar.get(2) + 1.also { monthSolar = it }
                calendar.get(1).also { yearSolar = it }
                animationRightSwipe()
                setImageBackground()
                setQuote()
            }

            override fun onSwipeLeft() {
                loadDataCalendar(daySolar, monthSolar, yearSolar)
                calendar.add(5, 1)
                calendar.get(5).also { daySolar = it }
                (calendar.get(2) + 1).also { monthSolar = it }
                calendar.get(1).also { yearSolar = it }
                animationLeftSwipe()
                setImageBackground()
                setQuote()
            }

            override fun onSwipeUp() {
                calendar.add(2, -1)
                calendar.get(5).also { daySolar = it }
                (calendar.get(2) + 1).also { monthSolar = it }
                calendar.get(1).also { yearSolar = it }
                loadDataCalendar(daySolar, monthSolar, yearSolar)
                animationTopSwipe()
                setImageBackground()
                setQuote()
            }

            override fun onSwipeDown() {
                calendar.add(2, 1)
                calendar.get(5).also { daySolar = it }
                (calendar.get(2) + 1).also { monthSolar = it }
                calendar.get(1).also { yearSolar = it }
                loadDataCalendar(daySolar, monthSolar, yearSolar)
                animationBottomSwipe()
                setImageBackground()
                setQuote()
            }

            override fun onClick() {
                val intent = Intent(context, DetailDayCalendarActivity::class.java)
                val bundle = Bundle()
                bundle.apply {
                    putInt(DAY_SOLAR, daySolar)
                    putInt(MONTH_SOLAR, monthSolar)
                    putInt(YEAR_SOLAR, yearSolar)
                }
                intent.putExtras(bundle)
                startActivity(intent)
                super.onClick()
            }
        })

    }

    private fun animationLeftSwipe() {
        val translationAnimation = TranslateAnimation(0f, (-getWidthScreen().toFloat()), 0f, 0f)
        translationAnimation.duration = Constant.TIME_MILLISECOND_400L
        binding.cardLunar.startAnimation(translationAnimation)
        binding.linearLayout2.startAnimation(translationAnimation)
        translationAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                val calendarDayFragment: DayCalendarFragment = this@DayCalendarFragment
                calendarDayFragment.loadDataCalendar(
                    calendarDayFragment.daySolar,
                    calendarDayFragment.monthSolar,
                    calendarDayFragment.yearSolar
                )
                val translateAnimation = TranslateAnimation(
                    getWidthScreen().toFloat(),
                    0f,
                    0f,
                    0f
                )
                translateAnimation.duration = Constant.TIME_MILLISECOND_400L
                binding.cardLunar.startAnimation(translateAnimation)
                binding.linearLayout2.startAnimation(translateAnimation)
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })
    }

    private fun animationRightSwipe() {
        val translationAnimation = TranslateAnimation(0f, getWidthScreen().toFloat(), 0f, 0f)
        translationAnimation.duration = Constant.TIME_MILLISECOND_400L
        binding.cardLunar.startAnimation(translationAnimation)
        binding.linearLayout2.startAnimation(translationAnimation)
        translationAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                val calendarDayFragment: DayCalendarFragment = this@DayCalendarFragment
                calendarDayFragment.loadDataCalendar(
                    calendarDayFragment.daySolar,
                    calendarDayFragment.monthSolar,
                    calendarDayFragment.yearSolar
                )
                val translateAnimation = TranslateAnimation(
                    -getWidthScreen().toFloat(),
                    0f,
                    0f,
                    0f
                )
                translateAnimation.duration = Constant.TIME_MILLISECOND_400L
                binding.cardLunar.startAnimation(translateAnimation)
                binding.linearLayout2.startAnimation(translateAnimation)
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })
    }

    private fun animationTopSwipe() {
        val translationAnimation = TranslateAnimation(0f, 0f, 450f, 0f)
        translationAnimation.duration = Constant.TIME_MILLISECOND_400L
        binding.tvDaySolar.startAnimation(translationAnimation)
        binding.linearLayout2.startAnimation(translationAnimation)
    }

    private fun animationBottomSwipe() {
        val translationAnimation = TranslateAnimation(0f, 0f, -450f, 0f)
        translationAnimation.duration = Constant.TIME_MILLISECOND_400L
        binding.tvDaySolar.startAnimation(translationAnimation)
        binding.linearLayout2.startAnimation(translationAnimation)
    }

    private fun updateTitleCurrentDate(monthSolar: Int, yearSolar: Int) {
        val currentDateString = "Tháng $monthSolar - $yearSolar"
        binding.tvCurrentDate.text = currentDateString
    }

    private fun isCurrentDate(daySolar: Int, monthSolar: Int, yearSolar: Int): Boolean {
        return daySolar == DateUtils.getDay() && monthSolar == DateUtils.getMonth() && yearSolar == DateUtils.getYear()
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        binding.tvHour.formatDateTime(calendar.time)
    }

    override fun onUpdateDateAfterPick(solarDay: String, solarMonth: String, solarYear: String) {
        try {
            daySolar = solarDay.toInt()
            monthSolar = solarMonth.toInt()
            yearSolar = solarYear.toInt()
            loadDataCalendar(daySolar, monthSolar, yearSolar)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}