package com.example.universalcalendar.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.universalcalendar.R
import com.example.universalcalendar.extensions.DateUtils
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
            is ViewHolderEventMonth -> holder.bindData(data, position, list)
            is ViewHolderEventDetail -> holder.bindData(data, position, list)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].viewType.ordinal
    }

    class ViewHolderEventMonth(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(item: EventDto, position: Int, list: List<EventDto>) {
            itemView.tv_item_event_date?.text = item.day
            itemView.tv_item_event_content?.text = item.contentEvent
            if (position == list.size - 1) itemView.item_event_divider?.visibility = View.INVISIBLE
            else itemView.item_event_divider?.visibility = View.VISIBLE
            val isDateWithPreviousItem = position > 0 && isCheckEventsInOneDay(list[position-1], list[position])
            itemView.tv_item_event_date?.visibility = if (!isDateWithPreviousItem) View.VISIBLE else View.INVISIBLE
            val timeStart = DateUtils.convertDateToString(
                DateUtils.convertStringToDate(DateUtils.DATE_EVENT_REGISTER, item.timeStart),
                DateUtils.DATE_EVENT_REGISTER_2
            )
            val timeEnd = DateUtils.convertDateToString(
                DateUtils.convertStringToDate(DateUtils.DATE_EVENT_REGISTER, item.timeEnd),
                DateUtils.DATE_EVENT_REGISTER_2
            )
            val address = if (item.address.isNullOrEmpty()) "" else  "${item.address}"
            itemView.tv_item_event_type_setup?.text = if (item.timeStart.isNullOrEmpty() || item.timeStart == item.timeEnd) "Cả ngày" else "$address, $timeStart - $timeEnd"
        }

        private fun isCheckEventsInOneDay(event1: EventDto, event2: EventDto): Boolean {
            return event1.day == event2.day && event1.month == event2.month && event1.year == event2.year
        }
    }

    class ViewHolderEventDetail(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(item: EventDto, position: Int, list: List<EventDto>) {
            itemView.tv_item_event_date?.text = item.day
            itemView.tv_item_event_content?.text = item.contentEvent
            itemView.ic_event.visibility = View.GONE
            val isDateWithPreviousItem = position > 0 && isCheckEventsInOneDay(list[position-1], list[position])
            itemView.tv_item_event_month_year.visibility = if (!isDateWithPreviousItem) View.VISIBLE else View.INVISIBLE
            itemView.tv_item_event_month_year.text = "${item.month}/${item.year}"
            itemView.tv_item_event_date?.visibility = if (!isDateWithPreviousItem) View.VISIBLE else View.INVISIBLE
            val timeStart = DateUtils.convertDateToString(
                DateUtils.convertStringToDate(DateUtils.DATE_EVENT_REGISTER, item.timeStart),
                DateUtils.DATE_EVENT_REGISTER_2
            )
            val timeEnd = DateUtils.convertDateToString(
                DateUtils.convertStringToDate(DateUtils.DATE_EVENT_REGISTER, item.timeEnd),
                DateUtils.DATE_EVENT_REGISTER_2
            )
            val address = if (item.address.isNullOrEmpty()) "" else  "${item.address}"
            itemView.tv_item_event_type_setup?.text =
                if (item.timeStart.isNullOrEmpty() || item.timeStart == item.timeEnd) "Cả ngày" else "$address, $timeStart - $timeEnd"
        }

        private fun isCheckEventsInOneDay(event1: EventDto, event2: EventDto): Boolean {
            return event1.day == event2.day && event1.month == event2.month && event1.year == event2.year
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshData(list: List<EventDto>) {
        this.list = list
        notifyDataSetChanged()
    }

}
