package com.example.universalcalendar.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universalcalendar.R
import kotlinx.android.synthetic.main.item_time_countries.view.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.zone.ZoneRulesException

class TimeCountriesAdapter(var list: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        fun bindData(item: String) {
//            try {
                val timeToday = LocalDateTime.now(ZoneId.of(item))
                val countries = item.replace("/", ",").replace("_", " ")
                val formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy")
                val dateCountries = timeToday.format(formatter)
                itemView.tv_time_countries_content?.text = countries
                itemView.tv_item_event_type_setup?.text = dateCountries
//            } catch (ex: ZoneRulesException) {
//                itemView.visibility = View.GONE
//            }

        }
    }

}
