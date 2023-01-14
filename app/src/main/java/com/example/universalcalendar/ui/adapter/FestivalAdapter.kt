package com.example.universalcalendar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.universalcalendar.databinding.ItemFestivalBinding
import com.example.universalcalendar.extensions.DiffCallBack
import com.example.universalcalendar.extensions.OnClickItem
import com.example.universalcalendar.model.Festival

class FestivalAdapter: ListAdapter<Festival, FestivalAdapter.FestivalViewHolder>(DiffCallBack<Festival>()){
    var listener: OnClickItem<Festival>?= null
    inner class FestivalViewHolder(private val viewBinding: ItemFestivalBinding): RecyclerView.ViewHolder(viewBinding.root) {
        fun bindData(festival: Festival) {
            itemView.run {
                viewBinding.festival = festival
                viewBinding.executePendingBindings()
                setOnClickListener {
                    listener?.onClick(festival)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FestivalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFestivalBinding.inflate(inflater, parent, false)
        return FestivalViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: FestivalViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }
}