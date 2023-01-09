package com.example.universalcalendar.ui.feature.daycalendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.core.content.ContextCompat
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.databinding.FragmentDayCalendarBinding
import com.example.universalcalendar.extensions.DateUtils
import com.example.universalcalendar.extensions.Utils
import com.example.universalcalendar.extensions.formatDateTime
import com.example.universalcalendar.model.Quotation
import com.example.universalcalendar.model.Quote
import com.example.universalcalendar.ui.base.BaseFragment
import com.example.universalcalendar.widgets.OnSwipeTouchListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_day_calendar.*
import java.util.*
import kotlin.collections.ArrayList

class DayCalendarFragment : BaseFragment<FragmentDayCalendarBinding, DayViewModel>() {
    private var daySolar = 0
    private var monthSolar = 0
    private var yearSolar = 0
    private val mListQuotation = ArrayList<Quotation>()

    private lateinit var calendar: Calendar
    override fun getViewModelClass(): Class<DayViewModel> = DayViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_day_calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendar = Calendar.getInstance()
    }

    override fun initView() {
        daySolar = calendar.get(5)
        monthSolar = calendar.get(2) + 1
        yearSolar = calendar.get(1)
        loadDataCalendar(daySolar, monthSolar, yearSolar)
        getListQuoteFromAsset()
        setQuote()
        setImageBackground()
        loadDataWhenSwipe()
    }

    private fun getListQuoteFromAsset() {
            val type = object : TypeToken<List<Quote>>() {}.type
            val jsonFromAssets = Utils.getDataFromAsset(requireContext(), Constant.QUOTE) ?: ""
            val listQuotation = Gson().fromJson<List<Quote>>(jsonFromAssets, type)
            if (jsonFromAssets.isNotEmpty() && listQuotation != null && listQuotation.isNotEmpty()) {
                mListQuotation.addAll(listQuotation[Constant.INDEX_DEFAULT].list_dn as List<Quotation>)
            }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setImageBackground() {
        if (image_background != null) {
            try {
                when (Random().nextInt(5)) {
                    0 -> {
                        image_background.setImageDrawable(resources.getDrawable(R.drawable.bg_lich0))
                    }

                    1 -> {
                        image_background.setImageDrawable(resources.getDrawable(R.drawable.bg_lich1))
                    }

                    2 -> {
                        image_background.setImageDrawable(resources.getDrawable(R.drawable.bg_lich2))
                    }

                    3 -> {
                        image_background.setImageDrawable(resources.getDrawable(R.drawable.bg_lich3))
                    }

                    4 -> {
                        image_background.setImageDrawable(resources.getDrawable(R.drawable.bg_lich4))
                    }

                    else -> {
                        image_background.setImageDrawable(resources.getDrawable(R.drawable.bg_lich0))
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
            }
        }
    }

    private fun setQuote() {
        val randomQuote = Random().nextInt(mListQuotation.size)
        tv_quote.text = mListQuotation[randomQuote].quote
        tv_author.text = mListQuotation[randomQuote].author
    }

    @SuppressLint("SetTextI18n")
    private fun loadDataCalendar(daySolar: Int, monthSolar: Int, yearSolar: Int) {
        val lunarDay = DateUtils.convertSolar2Lunar(daySolar, monthSolar, yearSolar, 7.00)
        tv_hour.formatDateTime(calendar.time)
        tv_day_solar.text = daySolar.toString()
        tv_day_lunar.text = lunarDay[0].toString()
        tv_month_lunar.text = "Tháng ${lunarDay[1]}"
        //calculate canchiday using solar date
        day_can_chi.text = DateUtils.getCanChiDayLunar(daySolar, monthSolar, yearSolar)
        month_can_chi.text = DateUtils.getCanChiForMonth(lunarDay[1], lunarDay[2])
        year_can_chi.text = "Năm ${DateUtils.getCanYearLunar(lunarDay[2])} ${DateUtils.getChiYearLunar(lunarDay[2])}"
        dayOfWeek.text = DateUtils.getWeek(yearSolar, monthSolar, daySolar)

        val isGoodDay = DateUtils.isGoodDay(
            DateUtils.getChiDayLunar(daySolar, monthSolar, yearSolar),
            lunarDay[1]
        )

        val isBadDay = DateUtils.isBadDay(
            DateUtils.getChiDayLunar(daySolar, monthSolar, yearSolar),
            lunarDay[1]
        )

        if (isGoodDay) {
            tv_status_day.text = resources.getString(R.string.tv_hoang_dao)
            tv_status_day.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        } else if (isBadDay) {
            tv_status_day.text = resources.getString(R.string.tv_hac_dao)
            tv_status_day.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
        } else {
            tv_status_day.text = Constant.EMPTY
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun loadDataWhenSwipe() {
        image_background.setOnTouchListener(object : OnSwipeTouchListener(activity) {
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
                loadDataCalendar(daySolar, monthSolar, yearSolar,)
                animationBottomSwipe()
                setImageBackground()
                setQuote()
            }
        })

    }

    private fun animationLeftSwipe() {
        val translationAnimation = TranslateAnimation(0f, (-getWidthScreen().toFloat()), 0f, 0f)
        translationAnimation.duration = Constant.TIME_MILLISECOND_400L
        card_lunar.startAnimation(translationAnimation)
        linearLayout2.startAnimation(translationAnimation)
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
                card_lunar.startAnimation(translateAnimation)
                linearLayout2.startAnimation(translateAnimation)
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })
    }

    private fun animationRightSwipe() {
        val translationAnimation = TranslateAnimation(0f, getWidthScreen().toFloat(), 0f, 0f)
        translationAnimation.duration = Constant.TIME_MILLISECOND_400L
        card_lunar.startAnimation(translationAnimation)
        linearLayout2.startAnimation(translationAnimation)
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
                card_lunar.startAnimation(translateAnimation)
                linearLayout2.startAnimation(translateAnimation)
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })
    }

    private fun animationTopSwipe() {
        val translationAnimation = TranslateAnimation(0f, 0f, 450f, 0f)
        translationAnimation.duration = Constant.TIME_MILLISECOND_400L
        tv_day_solar.startAnimation(translationAnimation)
        linearLayout2.startAnimation(translationAnimation)
    }

    private fun animationBottomSwipe() {
        val translationAnimation = TranslateAnimation(0f, 0f, -450f, 0f)
        translationAnimation.duration = Constant.TIME_MILLISECOND_400L
        tv_day_solar.startAnimation(translationAnimation)
        linearLayout2.startAnimation(translationAnimation)
    }

}