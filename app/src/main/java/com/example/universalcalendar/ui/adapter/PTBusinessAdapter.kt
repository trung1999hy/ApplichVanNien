package com.example.universalcalendar.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.universalcalendar.databinding.ItemFestivalBinding
import com.example.universalcalendar.databinding.ItemVowsBinding
import com.example.universalcalendar.extensions.DiffCallBack
import com.example.universalcalendar.extensions.OnClickItem
import com.example.universalcalendar.model.Festival
import com.example.universalcalendar.model.PTBusiness

class PTBusinessAdapter: ListAdapter<PTBusiness, PTBusinessAdapter.PTBusinessViewHolder>(DiffCallBack<PTBusiness>()) {
    var listener: OnClickItem<PTBusiness>? = null

    inner class PTBusinessViewHolder(private val viewBinding: ItemVowsBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {
        fun bindData(ptBusiness: PTBusiness) {
            itemView.run {
                viewBinding.ptBusiness = ptBusiness
                viewBinding.executePendingBindings()
                setOnClickListener {
                    listener?.onClick(ptBusiness)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PTBusinessViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemVowsBinding.inflate(inflater, parent, false)
        return PTBusinessViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: PTBusinessViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }
}