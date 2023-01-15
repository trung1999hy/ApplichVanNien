package com.example.universalcalendar.ui.feature.setting

import android.view.View
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.FragmentSettingBinding
import com.example.universalcalendar.extensions.SharePreference
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.ui.base.BaseFragment
import com.example.universalcalendar.ui.dialog.UserDialog

class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>() {

    override fun getViewModelClass(): Class<SettingViewModel> = SettingViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_setting

    override fun initView() {
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
    }

    override fun initData() {

    }

}