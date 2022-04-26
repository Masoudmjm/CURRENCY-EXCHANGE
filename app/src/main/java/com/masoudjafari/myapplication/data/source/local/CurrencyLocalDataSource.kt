package com.masoudjafari.myapplication.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.masoudjafari.myapplication.data.Balance
import com.masoudjafari.myapplication.data.CurrencyExchangeRate
import com.masoudjafari.myapplication.data.Result
import com.masoudjafari.myapplication.data.Transaction
import com.masoudjafari.myapplication.data.source.CurrencyDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CurrencyLocalDataSource internal constructor(
    private val currencyExchangeRateDao: CurrencyExchangeRateDao,
    private val balanceDao: BalanceDao,
    private val transactionDao: TransactionDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CurrencyDataSource {

    override suspend fun getCurrencyRates(): Result<List<CurrencyExchangeRate>> =
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(currencyExchangeRateDao.getAll())
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun getCurrencyRate(currencyName: String): Result<CurrencyExchangeRate> =
        withContext(ioDispatcher) {
            try {
                return@withContext Result.Success(
                    currencyExchangeRateDao.getCurrencyRate(
                        currencyName
                    )
                )
            } catch (e: Exception) {
                return@withContext Result.Error(e)
            }
        }

    override suspend fun saveCurrencyRate(currencyRate: CurrencyExchangeRate) {
        currencyExchangeRateDao.insertCurrency(currencyRate)
    }

    override suspend fun deleteAllCurrencyRates() = withContext(ioDispatcher) {
        currencyExchangeRateDao.deleteCurrencies()
    }

    override suspend fun getBalances(): Result<List<Balance>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(balanceDao.getAllBalances())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getBalance(currencyName: String): Result<Balance> =
        withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(balanceDao.getBalance(currencyName))
            } catch (e: Exception) {
                Result.Error(e)
            }
        }

    override suspend fun saveBalance(balance: Balance) {
        balanceDao.insertBalance(balance)
    }

    override suspend fun updateBalance(balance: Balance) = withContext(ioDispatcher) {
        balanceDao.updateBalance(balance)
    }

    override suspend fun getTransactions(): Result<List<Transaction>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(transactionDao.getAllTransactions())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }
}