package com.example.universalcalendar.ui.dialog

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.universalcalendar.R
import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.extensions.click
import com.example.universalcalendar.model.TimeCountry
import com.example.universalcalendar.ui.adapter.TimeCountriesAdapter
import com.example.universalcalendar.ui.base.BaseDialog
import kotlinx.android.synthetic.main.dialog_time_contries.view.*
import kotlinx.android.synthetic.main.dialog_user_login.view.*
import kotlinx.android.synthetic.main.item_time_countries.view.*
import okhttp3.internal.toImmutableList
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.zone.ZoneRulesException


class TimeCountriesDialog : BaseDialog() {

    companion object {
        fun newInstance() = TimeCountriesDialog()
    }

    private lateinit var mView: View
    private var listTime: ArrayList<TimeCountry> = arrayListOf()
    private var listTimeFilter: ArrayList<TimeCountry> = arrayListOf()
    private var adapter: TimeCountriesAdapter? = null

    override fun createCustomView(): View {
        mView = View.inflate(context, R.layout.dialog_time_contries, null)
        initView()
        return mView
    }

    private fun initView() {
        mView.ic_tv_time_countries_title_close.click { dismissDialog() }
        mView.ic_tv_time_countries_title_close.click { dismissDialog() }
        val listTimeData = Constant.TimeCountries.LIST_TIME_COUNTRIES.split(",")
        listTimeData.forEach {
            try {
                val timeToday = LocalDateTime.now(ZoneId.of(it))
                val countries = it.replace("/", ", ").replace("_", " ")
                val formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy")
                val dateCountries = timeToday.format(formatter)
                listTime.add(
                    TimeCountry(
                        countries,
                        dateCountries
                    )
                )
            } catch (ex: ZoneRulesException) {
                //
            }
        }
        listTimeFilter.addAll(listTime)
        adapter = TimeCountriesAdapter(listTime)
        mView.rv_time_countries?.layoutManager = LinearLayoutManager(context)
        mView.rv_time_countries?.adapter = adapter
        mView.rv_time_countries?.adapter = adapter
        mView.et_time_countries_search?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                listTimeFilter.clear()
                if (p0.isNullOrEmpty()) {
                    adapter?.refreshData(listTime)
                } else {
                    listTimeFilter.addAll(
                        listTime.filter { it.name?.contains(p0, true) ?: false }
                    )
                    adapter?.refreshData(listTimeFilter)
                    if (listTimeFilter.isEmpty()) {
                        mView.rv_time_countries?.visibility = View.GONE
                        mView.ttvFilterError?.visibility = View.VISIBLE
                    } else {
                        mView.rv_time_countries?.visibility = View.VISIBLE
                        mView.ttvFilterError?.visibility = View.GONE
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun shows(fm: FragmentManager): BaseDialog {
        show(fm, TimeCountriesDialog::class.java.name)
        return this
    }

}