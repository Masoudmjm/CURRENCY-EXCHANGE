package com.masoudjafari.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Transaction(
    val soledCurrency: String,
    val receivedCurrency: String,
    val amount: Double,
    val commission: Double,
    val date: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
