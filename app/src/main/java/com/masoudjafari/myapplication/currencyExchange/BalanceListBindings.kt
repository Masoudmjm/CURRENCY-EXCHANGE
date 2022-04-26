package com.masoudjafari.myapplication.currencyExchange

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.masoudjafari.myapplication.data.Balance
import com.masoudjafari.myapplication.data.CurrencyExchangeRate

/**
 * [BindingAdapter]s for the [Balance]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Balance>?) {
    items?.let {
        (listView.adapter as BalanceAdapter).submitList(items)
    }
}
