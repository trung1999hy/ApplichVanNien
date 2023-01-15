package com.example.universalcalendar.ui.feature.setting.knowledge

import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivityDetailKnowledgeBinding
import com.example.universalcalendar.databinding.ActivityDetailPtBusinessBinding
import com.example.universalcalendar.model.PTBusiness
import com.example.universalcalendar.ui.base.BaseActivity
import com.example.universalcalendar.ui.feature.setting.business.ListPTBusinessActivity

class DetailKnowledgeActivity : BaseActivity<ActivityDetailKnowledgeBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_detail_knowledge

    override fun initView() {
        val ptBusiness = intent?.extras?.getSerializable(ListPTBusinessActivity.PT_BUSINESS) as? PTBusiness
        ptBusiness?.description?.let {
            binding.webview.loadDataWithBaseURL(null,
                it, "text/html", "UTF-8", null)
        }
    }
}
