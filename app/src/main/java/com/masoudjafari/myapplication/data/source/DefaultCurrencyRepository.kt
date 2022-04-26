package com.masoudjafari.myapplication.data.source

import androidx.lifecycle.LiveData
import com.masoudjafari.myapplication.data.Balance
import com.masoudjafari.myapplication.data.CurrencyExchangeRate
import com.masoudjafari.myapplication.util.wrapEspressoIdlingResource
import com.masoudjafari.myapplication.data.Result
import com.masoudjafari.myapplication.data.Transaction

class DefaultCurrencyRepository(
    private val currencyRemoteDataSource: CurrencyDataSource,
    private val currencyLocalDataSource: CurrencyDataSource,
) : CurrencyRepository {

    override suspend fun getCurrencyRates(forceUpdate: Boolean): Result<List<CurrencyExchangeRate>> {
        if (forceUpdate) {
            try {
                updateCurrenciesRateFromRemoteDataSource()
            } catch (ex: Exception) {
                return Result.Error(ex)
            }
        }
        return currencyLocalDataSource.getCurrencyRates()
    }

    override suspend fun getCurrencyRate(currencyName: String): Result<CurrencyExchangeRate> {
        return currencyLocalDataSource.getCurrencyRate(currencyName)
    }

    private suspend fun updateCurrenciesRateFromRemoteDataSource() {
        val remoteCurrenciesRate = currencyRemoteDataSource.getCurrencyRates()

        if (remoteCurrenciesRate is Result.Success) {
            currencyLocalDataSource.deleteAllCurrencyRates()
            remoteCurrenciesRate.data.forEach { currencyRate ->
                currencyLocalDataSource.saveCurrencyRate(currencyRate)
            }

        } else if (remoteCurrenciesRate is Result.Error) {
            throw remoteCurrenciesRate.exception
        }
    }

    override suspend fun saveCurrencyRate(currencyRate: CurrencyExchangeRate) {
        currencyLocalDataSource.saveCurrencyRate(currencyRate)
    }

    override suspend fun deleteAllCurrencyRates() {
        currencyLocalDataSource.deleteAllCurrencyRates()
    }

    override suspend fun getBalances(): Result<List<Balance>> {
        val balances = currencyLocalDataSource.getBalances()
        if (balances is Result.Success) {
            if (balances.data.isEmpty()) {
                val balance = Balance("eUR", 1000.0)
                saveBalance(balance)
            }
        }
        return currencyLocalDataSource.getBalances()
    }

    override suspend fun getBalance(currencyName: String): Result<Balance> {
        return currencyLocalDataSource.getBalance(currencyName)
    }

    override suspend fun saveBalance(balance: Balance) {
        currencyLocalDataSource.saveBalance(balance)
    }

    override suspend fun updateBalance(balance: Balance) {
        currencyLocalDataSource.updateBalance(balance)
    }

    override suspend fun getTransactions(): Result<List<Transaction>> {
        return currencyLocalDataSource.getTransactions()
    }

    override suspend fun saveTransaction(transaction: Transaction) {
        currencyLocalDataSource.saveTransaction(transaction)
    }
}