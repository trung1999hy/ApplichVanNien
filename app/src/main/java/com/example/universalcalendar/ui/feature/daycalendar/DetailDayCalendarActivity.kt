package com.example.universalcalendar.ui.feature.daycalendar

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import androidx.core.content.ContextCompat
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.databinding.ActivityDayDetailBinding
import com.example.universalcalendar.extensions.DateUtils
import com.example.universalcalendar.ui.base.BaseActivity
import com.example.universalcalendar.ui.base.BaseFragment
import java.util.Date

class DetailDayCalendarActivity : BaseActivity<ActivityDayDetailBinding>() {

    private var daySolar = 0
    private var monthSolar = 0
    private var yearSolar = 0


    override fun getLayoutId(): Int = R.layout.activity_day_detail

    @SuppressLint("SetTextI18n")
    override fun initView() {
        daySolar = intent?.extras?.getInt(DayCalendarFragment.DAY_SOLAR) ?: Constant.INDEX_DEFAULT
        monthSolar = intent?.extras?.getInt(DayCalendarFragment.MONTH_SOLAR) ?: Constant.INDEX_DEFAULT
        yearSolar = intent?.extras?.getInt(DayCalendarFragment.YEAR_SOLAR) ?: Constant.INDEX_DEFAULT
        val lunarDay = DateUtils.convertSolar2Lunar(daySolar, monthSolar, yearSolar, 7.00)
        binding.tvDateDetail.text = "${DateUtils.getWeek(yearSolar, monthSolar, daySolar)}, $daySolar/$monthSolar/$yearSolar"
        binding.tvDateLunar.text = "${lunarDay[0]} - ${lunarDay[1]} - ${lunarDay[2]}"
        val dayCanChi = DateUtils.getCanChiDayLunar(daySolar, monthSolar, yearSolar)
        val monthCanChi = DateUtils.getCanChiForMonth(lunarDay[1], lunarDay[2])
        val yearCanChi = "${DateUtils.getCanYearLunar(lunarDay[2])} ${DateUtils.getChiYearLunar(lunarDay[2])}"
        binding.tvCanChiDate.text = "$dayCanChi - $monthCanChi - $yearCanChi"
        setTextDayHD(lunarDay)
        binding.tvSolarTerm.text = DateUtils.getSolarTerm(daySolar, monthSolar, yearSolar)
        binding.tvDepart.text = DateUtils.getDepart(daySolar, monthSolar, yearSolar)
        binding.tvStar.text = Html.fromHtml(DateUtils.getStatusStar(yearSolar, lunarDay[1], lunarDay[0], monthSolar, daySolar))
        binding.tvNhiThap.text = Html.fromHtml(DateUtils.getTimeBetween(daySolar, monthSolar, yearSolar))
        binding.tvHourGood.text = DateUtils.getTimeGoodHour(daySolar, monthSolar, yearSolar)
    }

    private fun setTextDayHD(lunarDay: IntArray) {
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
            binding.tvStatusDay.setTextColor(ContextCompat.getColor(this, R.color.red))
        } else if (isBadDay) {
            binding.tvStatusDay.text = resources.getString(R.string.tv_hac_dao)
            binding.tvStatusDay.setTextColor(ContextCompat.getColor(this, R.color.gray))
        } else {
            binding.tvStatusDay.text = Constant.EMPTY
        }
    }

}