package com.example.universalcalendar.ui.feature.setting.vows

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalcalendar.R
import com.example.universalcalendar.databinding.ActivityListVowsBinding
import com.example.universalcalendar.extensions.OnClickItem
import com.example.universalcalendar.model.Vows
import com.example.universalcalendar.ui.adapter.IPositionListener
import com.example.universalcalendar.ui.adapter.VowsAdapter
import com.example.universalcalendar.ui.base.BaseActivity
import com.example.universalcalendar.ui.feature.setting.pt.DetailCustomsActivity
import com.example.universalcalendar.ui.feature.setting.pt.ListCustomsActivity

class ListVowsActivity : BaseActivity<ActivityListVowsBinding>(), IPositionListener {
    companion object {
        const val POSITION = "position"
    }
    private val mAdapterVows = VowsAdapter()
    private lateinit var viewModel: VowsViewModel
    override fun getLayoutId(): Int = R.layout.activity_list_vows

    override fun initView() {
        mAdapterVows.listener = this
        viewModel = ViewModelProvider(this)[VowsViewModel::class.java]
        viewModel.fetchDataFromJson(this)
        viewModel.listVows.observe(this) { listVows ->
            mAdapterVows.submitList(listVows)
            binding.rcvVows.adapter = mAdapterVows
            binding.rcvVows.layoutManager = LinearLayoutManager(this)
        }
    }

    override fun position(position: Int) {
        val intent = Intent(this, DetailVowsActivity::class.java).apply {
            val bundle = Bundle()
            bundle.putInt(POSITION, position)
            putExtras(bundle)
        }
        startActivity(intent)
    }
}