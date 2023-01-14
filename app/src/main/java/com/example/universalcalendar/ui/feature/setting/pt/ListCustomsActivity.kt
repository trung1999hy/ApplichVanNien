package com.example.universalcalendar.ui.feature.setting.pt

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivityListCustomsBinding
import com.example.universalcalendar.extensions.OnClickItem
import com.example.universalcalendar.model.Customs
import com.example.universalcalendar.ui.adapter.CustomsAdapter
import com.example.universalcalendar.ui.base.BaseActivity
import com.example.universalcalendar.ui.base.BaseFragment

class ListCustomsActivity : BaseActivity<ActivityListCustomsBinding>(), OnClickItem<Customs> {
    companion object {
        const val CUSTOMS = "customs"
    }
    private lateinit var viewModel: CustomsViewModel
    private val mAdapterCustoms = CustomsAdapter()
    override fun getLayoutId(): Int = R.layout.activity_list_customs

    override fun initView() {
        mAdapterCustoms.listener = this
        viewModel = ViewModelProvider(this)[CustomsViewModel::class.java]
        viewModel.fetchDataFromJson(this)
        viewModel.listCustoms.observe(this) { listCustoms ->
            mAdapterCustoms.submitList(listCustoms)
            binding.rcvListCustoms.adapter = mAdapterCustoms
            binding.rcvListCustoms.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onClick(data: Customs) {
        val intent = Intent(this, DetailCustomsActivity::class.java).apply {
            val bundle = Bundle()
            bundle.putSerializable(CUSTOMS, data)
            putExtras(bundle)
        }
        startActivity(intent)
    }
}