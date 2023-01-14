package com.example.universalcalendar.ui.feature.setting.festival

import android.annotation.SuppressLint
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivityDetailFestivalBinding
import com.example.universalcalendar.model.Customs
import com.example.universalcalendar.model.Festival
import com.example.universalcalendar.ui.base.BaseActivity
import com.example.universalcalendar.ui.feature.setting.pt.ListCustomsActivity

class FestivalDetailActivity : BaseActivity<ActivityDetailFestivalBinding>() {
    override fun getLayoutId(): Int = R.layout.activity_detail_festival

    @SuppressLint("SetTextI18n")
    override fun initView() {
        val festival = intent?.extras?.getSerializable(ListFestivalActivity.FESTIVAL) as? Festival
        binding.tvTitle.text = festival?.title
        festival?.container?.let {
            binding.webview.loadDataWithBaseURL(null,
                it, "text/html", "UTF-8", null)
        }
        binding.tvLocation.text = "Miền ${festival?.location}"
        binding.tvAddressFestival.text = festival?.address
        binding.tvTimeEvent.text = festival?.timeEvent
    }
}