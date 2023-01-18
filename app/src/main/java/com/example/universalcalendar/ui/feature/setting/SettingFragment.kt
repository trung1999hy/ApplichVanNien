package com.example.universalcalendar.ui.feature.setting

import android.content.Intent
import android.view.View
import com.example.universalcalendar.BuildConfig
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.FragmentSettingBinding
import com.example.universalcalendar.extensions.DateUtils
import com.example.universalcalendar.extensions.SharePreference
import com.example.universalcalendar.extensions.Utils
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.ui.base.BaseFragment
import com.example.universalcalendar.ui.dialog.TimeCountriesDialog
import com.example.universalcalendar.ui.feature.setting.business.ListPTBusinessActivity
import com.example.universalcalendar.ui.feature.setting.festival.ListFestivalActivity
import com.example.universalcalendar.ui.feature.setting.pt.ListCustomsActivity
import com.example.universalcalendar.ui.feature.setting.vows.ListVowsActivity
import com.example.universalcalendar.ui.dialog.UserDialog

class SettingFragment : BaseFragment<FragmentSettingBinding, SettingViewModel>() {

    private var listStateStar = arrayOf(0,0,0,0,0)
    private val packageName = activity?.packageName

    override fun getViewModelClass(): Class<SettingViewModel> = SettingViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_setting

    override fun initView() {
        binding.btnPt.setOnClickListener { startActivity(Intent(context, ListCustomsActivity::class.java)) }
        binding.btnFestival.setOnClickListener { startActivity(Intent(context, ListFestivalActivity::class.java)) }
        binding.btnVows.setOnClickListener { startActivity(Intent(context, ListVowsActivity::class.java)) }
        binding.btnBusinessPt.setOnClickListener { startActivity(Intent(context, ListPTBusinessActivity::class.java)) }
        binding.btnKnowledgePt.setOnClickListener { startActivity(Intent(context, ListPTBusinessActivity::class.java)) }
        binding.llShareApp.setOnClickListener {
            if (packageName != null) {
                Utils.shareApp(requireContext(), packageName)
            }
        }
        val userInfo = SharePreference.getInstance().getUserInformation()
        if (userInfo != null) {
            binding.tvSettingLogin.visibility = View.GONE
            binding.lloUserInfo.visibility = View.VISIBLE
            binding.tvUserName.text = userInfo.name
            binding.tvDob.text = DateUtils.convertDateToString(
                DateUtils.convertStringToDate(DateUtils.DATE_LOCALE_FORMAT_2, userInfo.dateOfBirth),
                DateUtils.DOB_FORMAT
            )
            binding.tvEmail.text = userInfo.email
        } else {
            binding.tvSettingLogin.visibility = View.VISIBLE
            binding.lloUserInfo.visibility = View.GONE
        }
        binding.tvSettingVersionContent.text = BuildConfig.VERSION_NAME
    }

    override fun initAction() {
        binding.tvSettingLogin.click {
            showDialogEditUserInfo()
        }
        binding.btnConvertHour.click {
            val dialog = TimeCountriesDialog.newInstance()
            dialog.shows(childFragmentManager)
        }
        binding.icRateStar1.setOnClickListener {
            val state = if (listStateStar[0] == 0) 1 else {
                listStateStar[1] = 0
                listStateStar[2] = 0
                listStateStar[3] = 0
                listStateStar[4] = 0
                0
            }
            listStateStar[0] = state
            updateStateStars()
        }
        binding.icRateStar2.setOnClickListener {
            val state = if (listStateStar[1] == 0) {
                listStateStar[0] = 1
                1
            } else {
                listStateStar[2] = 0
                listStateStar[3] = 0
                listStateStar[4] = 0
                0
            }
            listStateStar[1] = state
            updateStateStars()
        }
        binding.icRateStar3.setOnClickListener {
            val state = if (listStateStar[2] == 0) {
                listStateStar[0] = 1
                listStateStar[1] = 1
                1
            } else {
                listStateStar[3] = 0
                listStateStar[4] = 0
                0
            }
            listStateStar[2] = state
            updateStateStars()
        }
        binding.icRateStar4.setOnClickListener {
            val state = if (listStateStar[3] == 0) {
                listStateStar[0] = 1
                listStateStar[1] = 1
                listStateStar[2] = 1
                1
            } else {
                listStateStar[4] = 0
                0
            }
            listStateStar[3] = state
            updateStateStars()
        }
        binding.icRateStar5.setOnClickListener {
            val state = if (listStateStar[4] == 0) {
                listStateStar[0] = 1
                listStateStar[1] = 1
                listStateStar[2] = 1
                listStateStar[3] = 1
                1
            } else 0
            listStateStar[4] = state
            binding.icRateStar5.setImageLevel(state)
            updateStateStars()
        }
        binding.icUserInfoEdit.click {
            showDialogEditUserInfo()
        }
    }

    private fun showDialogEditUserInfo() {
        val userInforDialog = UserDialog.newInstance()
        userInforDialog.apply {
            userLoginCallback = { userName ->
                binding.tvSettingLogin.visibility = View.GONE
                binding.lloUserInfo.visibility = View.VISIBLE
                binding.tvUserName.text = userName.name
                binding.tvDob.text = DateUtils.convertDateToString(
                    DateUtils.convertStringToDate(DateUtils.DATE_LOCALE_FORMAT_2, userName.dateOfBirth),
                    DateUtils.DOB_FORMAT
                )
                binding.tvEmail.text = userName.email
            }
        }
        userInforDialog.shows(childFragmentManager)
    }

    override fun initData() {
        updateStateStars()
    }

    private fun updateStateStars() {
        binding.icRateStar1.setImageLevel(listStateStar[0])
        binding.icRateStar2.setImageLevel(listStateStar[1])
        binding.icRateStar3.setImageLevel(listStateStar[2])
        binding.icRateStar4.setImageLevel(listStateStar[3])
        binding.icRateStar5.setImageLevel(listStateStar[4])
    }

}