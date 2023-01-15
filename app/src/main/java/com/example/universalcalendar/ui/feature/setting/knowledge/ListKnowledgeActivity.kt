package com.example.universalcalendar.ui.feature.setting.knowledge

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivityListKnowledgeBinding
import com.example.universalcalendar.databinding.ActivityListVowsBinding
import com.example.universalcalendar.extensions.OnClickItem
import com.example.universalcalendar.model.PTBusiness
import com.example.universalcalendar.ui.adapter.PTBusinessAdapter
import com.example.universalcalendar.ui.base.BaseActivity
import com.example.universalcalendar.ui.feature.setting.business.DetailPTBusinessActivity
import com.example.universalcalendar.ui.feature.setting.business.PTBusinessViewModel

class ListKnowledgeActivity : BaseActivity<ActivityListKnowledgeBinding>(), OnClickItem<PTBusiness> {
    companion object {
        const val PT_BUSINESS = "pt_business"
    }

    private lateinit var viewModel: PTBusinessViewModel
    private val ptBusinessAdapter = PTBusinessAdapter()
    override fun getLayoutId(): Int = R.layout.activity_list_knowledge

    override fun initView() {
        ptBusinessAdapter.listener = this
        viewModel = ViewModelProvider(this)[PTBusinessViewModel::class.java]
        viewModel.fetchDataFromJson(this)
        viewModel.listPTBusiness.observe(this) { listPTBusiness ->
            ptBusinessAdapter.submitList(listPTBusiness)
            binding.rcvVows.adapter = ptBusinessAdapter
            binding.rcvVows.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun onClick(data: PTBusiness) {
        val intent = Intent(this, DetailKnowledgeActivity::class.java).apply {
            val bundle = Bundle()
            bundle.putSerializable(PT_BUSINESS, data)
            putExtras(bundle)
        }
        startActivity(intent)
    }
}