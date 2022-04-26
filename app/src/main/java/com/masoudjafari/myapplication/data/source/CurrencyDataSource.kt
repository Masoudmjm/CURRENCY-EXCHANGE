package com.masoudjafari.myapplication.data.source

import androidx.lifecycle.LiveData
import com.masoudjafari.myapplication.data.Balance
import com.masoudjafari.myapplication.data.CurrencyExchangeRate
import com.masoudjafari.myapplication.data.Result
import com.masoudjafari.myapplication.data.Transaction

interface CurrencyDataSource {

    // currencyExchangeRate
    suspend fun getCurrencyRates(): Result<List<CurrencyExchangeRate>>

    suspend fun getCurrencyRate(currencyName: String): Result<CurrencyExchangeRate>

    suspend fun saveCurrencyRate(currencyRate: CurrencyExchangeRate)

    suspend fun deleteAllCurrencyRates()

    //Balance
    suspend fun getBalances(): Result<List<Balance>>

    suspend fun getBalance(currencyName: String): Result<Balance>

    suspend fun saveBalance(balance: Balance)

    suspend fun updateBalance(balance: Balance)

    //Transaction
    suspend fun getTransactions(): Result<List<Transaction>>

    suspend fun saveTransaction(transaction: Transaction)
}