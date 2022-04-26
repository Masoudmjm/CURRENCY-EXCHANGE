package com.masoudjafari.myapplication.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.masoudjafari.myapplication.data.Balance
import com.masoudjafari.myapplication.data.CurrencyExchangeRate

@Dao
interface BalanceDao {

    @Query("SELECT * FROM Balance")
    fun observeBalance(): LiveData<List<Balance>>

    @Query("SELECT * FROM Balance")
    fun getAllBalances(): List<Balance>

    @Query("SELECT * FROM Balance WHERE currency = :name")
    suspend fun getBalance(name: String): Balance

    @Query("SELECT * FROM Balance WHERE currency = :name")
    fun observeBalanceByName(name: String): LiveData<Balance>

    @Query("DELETE FROM Balance")
    fun deleteBalances()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: Balance)

    @Update
    fun updateBalance(balance: Balance)

    @Query("UPDATE Balance SET balance = :balance WHERE currency = :name")
    fun updateBalance(name: String, balance: Double)
}