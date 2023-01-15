package com.example.universalcalendar.ui.feature.setting.business

import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivityDetailCustomsBinding
import com.example.universalcalendar.databinding.ActivityDetailPtBusinessBinding
import com.example.universalcalendar.databinding.ActivityDetailVowsBinding
import com.example.universalcalendar.model.PTBusiness
import com.example.universalcalendar.ui.base.BaseActivity

class DetailPTBusinessActivity : BaseActivity<ActivityDetailPtBusinessBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_detail_pt_business

    override fun initView() {
        val ptBusiness = intent?.extras?.getSerializable(ListPTBusinessActivity.PT_BUSINESS) as? PTBusiness
        ptBusiness?.description?.let {
            binding.webview.loadDataWithBaseURL(null,
                it, "text/html", "UTF-8", null)
        }
    }
}