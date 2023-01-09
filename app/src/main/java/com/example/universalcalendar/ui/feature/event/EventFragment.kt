package com.example.universalcalendar.ui.feature.event

import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.FragmentEventBinding
import com.example.universalcalendar.ui.base.BaseFragment


class EventFragment : BaseFragment<FragmentEventBinding, EventViewModel>() {

    override fun getViewModelClass(): Class<EventViewModel> = EventViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_event

    override fun initView() {
    }

    override fun initData() {

    }


}