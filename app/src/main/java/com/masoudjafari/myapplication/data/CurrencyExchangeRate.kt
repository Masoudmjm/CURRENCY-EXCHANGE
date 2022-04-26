package com.masoudjafari.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyExchangeRate(
    @PrimaryKey var currency: String,
    var base: String,
    var rate: Double,
    var date: String
)


