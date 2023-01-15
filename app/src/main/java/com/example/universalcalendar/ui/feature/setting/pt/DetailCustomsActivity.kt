package com.example.universalcalendar.ui.feature.setting.pt

import android.text.Html
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Strings
import com.example.universalcalendar.databinding.ActivityDetailCustomsBinding
import com.example.universalcalendar.model.Customs
import com.example.universalcalendar.ui.base.BaseActivity

class DetailCustomsActivity : BaseActivity<ActivityDetailCustomsBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_detail_customs

    override fun initView() {
        val customs = intent?.extras?.getSerializable(ListCustomsActivity.CUSTOMS) as? Customs
        binding.tvTitle.text = customs?.title
        customs?.container?.let {
            binding.webview.loadDataWithBaseURL(null,
                it, "text/html", "UTF-8", null)
        }
    }
}