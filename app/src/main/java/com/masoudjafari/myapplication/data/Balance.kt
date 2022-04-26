package com.masoudjafari.myapplication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Balance(
    @PrimaryKey val currency: String,
    val balance: Double
)
