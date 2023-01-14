package com.example.universalcalendar.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBindings
import com.example.universalcalendar.R
import com.example.universalcalendar.extensions.DiffCallBack
import com.example.universalcalendar.extensions.OnClickItem
import com.example.universalcalendar.model.Vows
import kotlinx.android.synthetic.main.item_header_vows.view.*
import kotlinx.android.synthetic.main.item_vows.view.*

class VowsAdapter : ListAdapter<Vows, ViewHolder>(DiffCallBack<Vows>()) {
    var listener: IPositionListener?= null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_header_vows, parent, false)
            )
            else -> TitleViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_vows, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        listener?.let { holder.bindData(getItem(position), it) }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isPositionHeader(position)) {
            TYPE_HEADER
        } else {
            TYPE_TITLE
        }
    }

    private fun isPositionHeader(position: Int): Boolean {
        return getItem(position).id == 0
    }
}

abstract class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    abstract fun bindData(vows: Vows, listener: IPositionListener)
}

class HeaderViewHolder(itemView: View) : ViewHolder(itemView) {
    override fun bindData(vows: Vows, listener: IPositionListener) {
        itemView.run {
            tv_header_vows.text = vows.title
        }
    }

}

class TitleViewHolder(itemView: View) : ViewHolder(itemView) {
    override fun bindData(vows: Vows, listener: IPositionListener) {
        itemView.run {
            tv_title_vows.text = vows.title
            setOnClickListener {
                listener.position(vows.id)
            }
        }
    }

}

interface IPositionListener {
    fun position(position: Int)
}

const val TYPE_HEADER = 0
const val TYPE_TITLE = 1