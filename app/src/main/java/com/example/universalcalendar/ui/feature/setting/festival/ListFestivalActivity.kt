package com.example.universalcalendar.ui.feature.setting.festival

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivityListFestivalBinding
import com.example.universalcalendar.extensions.OnClickItem
import com.example.universalcalendar.model.Festival
import com.example.universalcalendar.ui.adapter.FestivalAdapter
import com.example.universalcalendar.ui.base.BaseActivity
import com.example.universalcalendar.ui.feature.setting.pt.CustomsViewModel
import com.example.universalcalendar.ui.feature.setting.pt.DetailCustomsActivity
import com.example.universalcalendar.ui.feature.setting.pt.ListCustomsActivity

class ListFestivalActivity : BaseActivity<ActivityListFestivalBinding>(), OnClickItem<Festival> {
    companion object {
        const val FESTIVAL = "festival"
    }
    private lateinit var viewModel: FestivalViewModel
    private val festivalAdapter = FestivalAdapter()
    override fun getLayoutId(): Int = R.layout.activity_list_festival

    override fun initView() {
        festivalAdapter.listener = this
        viewModel = ViewModelProvider(this)[FestivalViewModel::class.java]
        viewModel.fetchDataFromJson(this)
        viewModel.listFestival.observe(this) { listCustoms ->
            festivalAdapter.submitList(listCustoms)
            binding.rcvListFestival.adapter = festivalAdapter
            binding.rcvListFestival.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onClick(data: Festival) {
        val intent = Intent(this, FestivalDetailActivity::class.java).apply {
            val bundle = Bundle()
            bundle.putSerializable(FESTIVAL, data)
            putExtras(bundle)
        }
        startActivity(intent)
    }
}