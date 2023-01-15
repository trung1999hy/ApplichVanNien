package com.example.universalcalendar.ui.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.universalcalendar.R
import com.example.universalcalendar.model.HourGood
import kotlinx.android.synthetic.main.item_hour_zodiac.view.*

class ZodiacAdapter(var list: List<HourGood>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolderListTime(View.inflate(parent.context, R.layout.item_hour_zodiac, null))
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderListTime).bindData(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolderListTime(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(item: HourGood) {
            itemView.run {
                Glide.with(context).load(Uri.parse("file:///android_asset/image/${item.icon}")).into(itemView.ic_zodiac)
                itemView.tv_name_zodiac.text = item.name
                itemView.tv_time_zodiac.text = item.hour
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(list: List<HourGood>) {
        this.list = list
        notifyDataSetChanged()
    }

}
