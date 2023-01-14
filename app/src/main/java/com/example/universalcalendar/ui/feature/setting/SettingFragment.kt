package com.example.universalcalendar.ui.feature.setting

import android.content.Intent
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.FragmentSettingBinding
import com.example.universalcalendar.ui.base.BaseFragment
import com.example.universalcalendar.ui.feature.setting.festival.ListFestivalActivity
import com.example.universalcalendar.ui.feature.setting.pt.ListCustomsActivity

class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>() {

    override fun getViewModelClass(): Class<SettingViewModel> = SettingViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_setting

    override fun initView() {
        binding.btnPt.setOnClickListener { startActivity(Intent(context, ListCustomsActivity::class.java)) }
        binding.btnFestival.setOnClickListener { startActivity(Intent(context, ListFestivalActivity::class.java)) }
    }

    override fun initData() {

    }

}