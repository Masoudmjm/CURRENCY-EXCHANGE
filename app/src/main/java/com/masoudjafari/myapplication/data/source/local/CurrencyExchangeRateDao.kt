package com.masoudjafari.myapplication.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.masoudjafari.myapplication.data.CurrencyExchangeRate

@Dao
interface CurrencyExchangeRateDao {

    @Query("SELECT * FROM CurrencyExchangeRate")
    fun observeCurrency(): LiveData<List<CurrencyExchangeRate>>

    @Query("SELECT * FROM CurrencyExchangeRate")
    fun getAll(): List<CurrencyExchangeRate>

    @Query("SELECT * FROM CurrencyExchangeRate WHERE currency = :name")
    suspend fun getCurrencyRate(name: String): CurrencyExchangeRate

    @Query("SELECT * FROM CurrencyExchangeRate WHERE currency = :name")
    fun observeCurrencyByName(name: String): CurrencyExchangeRate

    @Query("DELETE FROM CurrencyExchangeRate")
    fun deleteCurrencies()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(currencyExchangeRate: CurrencyExchangeRate)

    @Update
    fun updateBalance(currencyExchangeRate: CurrencyExchangeRate)

}