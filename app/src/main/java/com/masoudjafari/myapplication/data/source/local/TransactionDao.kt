package com.masoudjafari.myapplication.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.masoudjafari.myapplication.data.Balance
import com.masoudjafari.myapplication.data.Transaction

@Dao
interface TransactionDao {

    @Query("SELECT * FROM `Transaction`")
    fun observeTransaction(): LiveData<List<Transaction>>

    @Query("SELECT * FROM `Transaction`")
    fun getAllTransactions(): List<Transaction>

    @Query("SELECT * FROM `Transaction` WHERE soledCurrency = :name")
    suspend fun getTransactions(name: String): List<Transaction>

    @Query("SELECT * FROM `Transaction` WHERE soledCurrency = :name")
    fun observeTransactionsByName(name: String): LiveData<Transaction>

    @Query("DELETE FROM `Transaction`")
    fun deleteTransactions()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    fun updateTransaction(transaction: Transaction)

    @Query("UPDATE `Transaction` SET amount = :amount WHERE soledCurrency = :name")
    fun updateTransaction(name: String, amount: Double)
}