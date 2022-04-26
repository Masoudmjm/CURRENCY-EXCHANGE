package com.masoudjafari.myapplication.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.masoudjafari.myapplication.data.Balance
import com.masoudjafari.myapplication.data.CurrencyExchangeRate
import com.masoudjafari.myapplication.data.Transaction

@Database(entities = [CurrencyExchangeRate::class, Balance::class, Transaction::class], version = 1)
abstract class CurrencyDatabase : RoomDatabase() {
    abstract fun currencyExchangeRateDao(): CurrencyExchangeRateDao
    abstract fun balanceDao(): BalanceDao
    abstract fun transactionDao(): TransactionDao
}