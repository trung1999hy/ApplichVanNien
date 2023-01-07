package com.example.universalcalendar.ui.feature.monthcalendar

import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.FragmentMonthCalendarBinding
import com.example.universalcalendar.ui.base.BaseFragment

class MonthCalendarFragment : BaseFragment<FragmentMonthCalendarBinding, MonthViewModel>() {

    override fun getViewModelClass(): Class<MonthViewModel> = MonthViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_month_calendar

    override fun initView() {
    }

}