package com.example.universalcalendar.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.universalcalendar.R
import com.example.universalcalendar.ui.base.BaseFragment

class EventFragment : BaseFragment() {
    override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun initViews() {

    }

    override fun initData() {

    }
}