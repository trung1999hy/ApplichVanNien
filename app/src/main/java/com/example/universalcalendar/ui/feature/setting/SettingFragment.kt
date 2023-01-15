package com.example.universalcalendar.ui.feature.setting

import android.content.Intent
import android.view.View
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.FragmentSettingBinding
import com.example.universalcalendar.extensions.SharePreference
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.ui.base.BaseFragment
import com.example.universalcalendar.ui.dialog.TimeCountriesDialog
import com.example.universalcalendar.ui.dialog.TimeEventRegisterDialog
import com.example.universalcalendar.ui.feature.setting.business.ListPTBusinessActivity
import com.example.universalcalendar.ui.feature.setting.festival.ListFestivalActivity
import com.example.universalcalendar.ui.feature.setting.pt.ListCustomsActivity
import com.example.universalcalendar.ui.feature.setting.vows.ListVowsActivity
import com.example.universalcalendar.ui.dialog.UserDialog

class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>() {

    override fun getViewModelClass(): Class<SettingViewModel> = SettingViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_setting

    override fun initView() {
        binding.btnPt.setOnClickListener { startActivity(Intent(context, ListCustomsActivity::class.java)) }
        binding.btnFestival.setOnClickListener { startActivity(Intent(context, ListFestivalActivity::class.java)) }
        binding.btnVows.setOnClickListener { startActivity(Intent(context, ListVowsActivity::class.java)) }
        binding.btnBusinessPt.setOnClickListener { startActivity(Intent(context, ListPTBusinessActivity::class.java)) }
        binding.btnKnowledgePt.setOnClickListener { startActivity(Intent(context, ListPTBusinessActivity::class.java)) }
        val userInfo = SharePreference.getInstance().getUserInformation()
        if (userInfo != null) {
            binding.tvSettingLogin.visibility = View.GONE
            binding.tvSettingUserName.visibility = View.VISIBLE
            binding.tvSettingUserName.text = userInfo.name
        } else {
            binding.tvSettingLogin.visibility = View.VISIBLE
            binding.tvSettingUserName.visibility = View.GONE
        }
    }

    override fun initAction() {
        binding.tvSettingLogin.click {
            val userInforDialog = UserDialog.newInstance()
            userInforDialog.apply {
                userLoginCallback = { userName ->
                    binding.tvSettingLogin.visibility = View.GONE
                    binding.tvSettingUserName.visibility = View.VISIBLE
                    binding.tvSettingUserName.text = userName
                }
            }
            userInforDialog.shows(childFragmentManager)
        }
        binding.btnConvertHour.click {
            val dialog = TimeCountriesDialog.newInstance()
            dialog.shows(childFragmentManager)
        }
    }

    override fun initData() {

    }

}