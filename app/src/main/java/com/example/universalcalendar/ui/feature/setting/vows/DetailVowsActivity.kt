package com.example.universalcalendar.ui.feature.setting.vows

import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivityDetailVowsBinding
import com.example.universalcalendar.ui.base.BaseActivity

class DetailVowsActivity : BaseActivity<ActivityDetailVowsBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_detail_vows

    override fun initView() {
        val position = intent?.extras?.getInt(ListVowsActivity.POSITION) ?: 0
        binding.webview.loadUrl("file:///android_asset/vows/html/$position.html")
    }
}