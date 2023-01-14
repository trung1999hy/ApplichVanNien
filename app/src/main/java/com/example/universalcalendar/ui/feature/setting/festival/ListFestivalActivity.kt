package com.example.universalcalendar.ui.feature.setting.festival

import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivityListFestivalBinding
import com.example.universalcalendar.extensions.OnClickItem
import com.example.universalcalendar.model.Festival
import com.example.universalcalendar.ui.adapter.FestivalAdapter
import com.example.universalcalendar.ui.base.BaseActivity
import com.example.universalcalendar.ui.feature.setting.pt.CustomsViewModel

class ListFestivalActivity : BaseActivity<ActivityListFestivalBinding>(), OnClickItem<Festival> {
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

    }
}