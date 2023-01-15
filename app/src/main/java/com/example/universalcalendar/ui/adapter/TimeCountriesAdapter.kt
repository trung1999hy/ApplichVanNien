package com.example.universalcalendar.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universalcalendar.R
import com.example.universalcalendar.model.TimeCountry
import kotlinx.android.synthetic.main.item_time_countries.view.*

class TimeCountriesAdapter(var list: List<TimeCountry>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderListTime(View.inflate(parent.context, R.layout.item_time_countries, null))
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderListTime).bindData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolderListTime(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(item: TimeCountry) {
            itemView.tv_time_countries_content?.text = item.name
            itemView.tv_item_event_type_setup?.text = item.time
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(list: List<TimeCountry>) {
        this.list = list
        notifyDataSetChanged()
    }

}
