package com.example.universalcalendar.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universalcalendar.R
import com.example.universalcalendar.ui.feature.monthcalendar.entities.EventDto

class TimeCountriesAdapter(var list: List<String>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderListTime(View.inflate(parent.context, R.layout.item_event, null))
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = list[position]
//        holder.bindData(data, position, list)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolderListTime(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(item: String, position: Int, list: List<EventDto>) {

        }
    }

}
