package com.example.universalcalendar.ui.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.contentValuesOf
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.universalcalendar.databinding.ItemCustomsBinding
import com.example.universalcalendar.extensions.DiffCallBack
import com.example.universalcalendar.extensions.OnClickItem
import com.example.universalcalendar.model.Customs

class CustomsAdapter : ListAdapter<Customs, CustomsAdapter.CustomsViewHolder>(DiffCallBack<Customs>()){
    var listener: OnClickItem<Customs>?= null
    inner class CustomsViewHolder(private val viewBinding: ItemCustomsBinding): RecyclerView.ViewHolder(viewBinding.root) {
        fun bindData(customs: Customs) {
            itemView.run {
                Glide.with(context).load(Uri.parse("file:///android_asset/image/${customs.image}")).into(viewBinding.imgCustoms)
                viewBinding.customs = customs
                viewBinding.executePendingBindings()
                setOnClickListener {
                    listener?.onClick(customs)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCustomsBinding.inflate(inflater, parent, false)
        return CustomsViewHolder(
            binding
        )
    }

    override fun onBindViewHolder(holder: CustomsViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }
}
