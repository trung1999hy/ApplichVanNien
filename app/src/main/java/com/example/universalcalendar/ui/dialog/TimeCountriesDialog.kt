package com.example.universalcalendar.ui.dialog

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.ui.adapter.TimeCountriesAdapter
import com.example.universalcalendar.ui.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_time_contries.view.*


class TimeCountriesDialog : BaseDialog() {

    companion object {
        fun newInstance() = TimeCountriesDialog()
    }
    private lateinit var mView: View
    private var adapter: TimeCountriesAdapter? = null

    override fun createCustomView(): View {
        mView = View.inflate(context, R.layout.dialog_time_contries, null)
        initView()
        return mView
    }

    private fun initView() {
        mView.ic_tv_time_countries_title_close.click { dismissDialog() }
        mView.ic_tv_time_countries_title_close.click { dismissDialog() }
        val listTime = Constant.TimeCountries.LIST_TIME_COUNTRIES.split(",")
        adapter = TimeCountriesAdapter(listTime)
        mView.rv_time_countries?.layoutManager = LinearLayoutManager(context)
        mView.rv_time_countries?.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun shows(fm: FragmentManager): BaseDialog {
        show(fm, TimeCountriesDialog::class.java.name)
        return this
    }

}