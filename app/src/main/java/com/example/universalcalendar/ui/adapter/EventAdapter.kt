package com.example.universalcalendar.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universalcalendar.R
import com.example.universalcalendar.ui.feature.monthcalendar.entities.EventDto
import kotlinx.android.synthetic.main.item_event.view.*

class EventAdapter(var list: List<EventDto>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ItemViewType {
        EVENT_DAY, EVENT_DETAIL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ItemViewType.EVENT_DAY.ordinal -> ViewHolderEventMonth(View.inflate(parent.context, R.layout.item_event, null))
            else -> ViewHolderEventDetail(View.inflate(parent.context, R.layout.item_event, null))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = list[position]
        when (holder) {
            is ViewHolderEventMonth -> holder.bindData(data, position, list.size)
            is ViewHolderEventDetail -> holder.bindData(data, position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType.ordinal
    }

    class ViewHolderEventMonth(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(item: EventDto, position: Int, size: Int) {
            itemView.tv_item_event_date?.text = item.day
            itemView.tv_item_event_content?.text = item.contentEvent
            if (position == size - 1) itemView.item_event_divider?.visibility = View.INVISIBLE
            else itemView.item_event_divider?.visibility = View.VISIBLE
        }
    }

    class ViewHolderEventDetail(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(item: EventDto, position: Int) {

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(list: List<EventDto>) {
        this.list = list
        notifyDataSetChanged()
    }

}
