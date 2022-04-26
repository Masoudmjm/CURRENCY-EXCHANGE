package com.masoudjafari.myapplication.currencyExchange

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.masoudjafari.myapplication.data.Balance
import com.masoudjafari.myapplication.data.CurrencyExchangeRate
import com.masoudjafari.myapplication.databinding.ItemMyBalancesBinding

class BalanceAdapter(private val viewModel: CurrencyExchangeViewModel) : ListAdapter<Balance, ViewHolder>(
    ImagesDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }
}

class ViewHolder private constructor(val binding: ItemMyBalancesBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: CurrencyExchangeViewModel, item: Balance) {

        binding.viewmodel = viewModel
        binding.balance = item
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemMyBalancesBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }
}

class ImagesDiffCallback : DiffUtil.ItemCallback<Balance>() {
    override fun areItemsTheSame(oldItem: Balance, newItem: Balance): Boolean {
        return oldItem.currency == newItem.currency
    }

    override fun areContentsTheSame(oldItem: Balance, newItem: Balance): Boolean {
        return oldItem == newItem
    }
}
