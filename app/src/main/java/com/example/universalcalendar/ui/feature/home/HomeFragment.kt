package com.example.universalcalendar.ui.feature.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.FragmentHomeBinding
import com.example.universalcalendar.ui.base.BaseFragment

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override fun getViewModelClass(): Class<HomeViewModel> = HomeViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_home

    override fun initView() {

    }

    override fun initData() {

    }

}