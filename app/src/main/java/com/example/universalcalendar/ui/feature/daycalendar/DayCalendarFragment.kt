package com.example.universalcalendar.ui.feature.daycalendar

import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.FragmentDayCalendarBinding
import com.example.universalcalendar.ui.base.BaseFragment

class DayCalendarFragment : BaseFragment<FragmentDayCalendarBinding, DayViewModel>() {

    override fun getViewModelClass(): Class<DayViewModel> = DayViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_day_calendar

    override fun initView() {
    }

    override fun initData() {

    }

}