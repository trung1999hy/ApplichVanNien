package com.example.universalcalendar.ui.feature.eventregister

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.FragmentEventRegisterBinding
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.ui.HomeActivity
import com.example.universalcalendar.ui.base.BaseFragment
import java.time.LocalDateTime


class EventRegisterFragment : BaseFragment<FragmentEventRegisterBinding, EventRegisterViewModel>() {

    private var currentDate : LocalDateTime = LocalDateTime.now()
    private var startDate : LocalDateTime = LocalDateTime.now()
    private var endDate : LocalDateTime = LocalDateTime.now()

    override fun getViewModelClass(): Class<EventRegisterViewModel> =
        EventRegisterViewModel::class.java

    override fun getLayoutId(): Int = R.layout.fragment_event_register

    override fun initView() {
        (activity as HomeActivity).onShowBottonBar(false)
        binding.etEventRegisterJobLocation.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.etEventRegisterJobLocation.clearFocus()
                val imm: InputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.etEventRegisterJobLocation.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }
        initAction()
    }

    override fun initAction() {
        binding.icEventRegisterBack.click { onBackPress() }

    }

    override fun initData() {
        //do nothing
    }

    private fun onBackPress() {
        (activity as HomeActivity).onShowBottonBar(true)
        (activity as HomeActivity).onBackPressed()
    }

    override fun handleBack() {
        binding.etEventRegisterJobLocation.clearFocus()
    }

}