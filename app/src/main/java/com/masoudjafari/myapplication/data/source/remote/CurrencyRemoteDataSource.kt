package com.masoudjafari.myapplication.data.source.remote

import androidx.lifecycle.MutableLiveData
import com.masoudjafari.myapplication.data.Balance
import com.masoudjafari.myapplication.data.CurrencyExchangeRate
import com.masoudjafari.myapplication.data.Result
import com.masoudjafari.myapplication.data.Transaction
import com.masoudjafari.myapplication.data.source.CurrencyDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.reflect.full.memberProperties

class CurrencyRemoteDataSource internal constructor(
    private val retrofitService: RetrofitService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : CurrencyDataSource {

    override suspend fun getCurrencyRates(): Result<List<CurrencyExchangeRate>> = withContext(ioDispatcher) {
        return@withContext try {
            val response = retrofitService.getLatest()

            val rates = response.rates
            val properties = rates!!::class.memberProperties
            val currencyRate: ArrayList<CurrencyExchangeRate> = ArrayList()

            properties.forEachIndexed { index, element ->
                val currency = element.name
                var rate = element.getter.call(rates)
                //TODO remove
                if (isInteger(rate.toString()))
                    rate = 1.0
                val item = CurrencyExchangeRate(
                    currency,
                    response.base.toString(),
                    rate as Double,
                    response.date.toString()
                )
                currencyRate.add(item)
            }

            Result.Success(currencyRate)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getCurrencyRate(currencyName: String): Result<CurrencyExchangeRate> {
        return Result.Error(Exception("not this way"))
    }

    override suspend fun saveCurrencyRate(currencyRate: CurrencyExchangeRate) {}

    override suspend fun deleteAllCurrencyRates() {}

    override suspend fun getBalances(): Result<List<Balance>> {
        return Result.Error(Exception("not this way"))
    }

    override suspend fun getBalance(currencyName: String): Result<Balance> {
        return Result.Error(Exception("not this way"))
    }

    override suspend fun saveBalance(balance: Balance) {}

    override suspend fun updateBalance(balance: Balance) {}

    override suspend fun getTransactions(): Result<List<Transaction>> {
        return Result.Error(Exception("not this way"))
    }

    override suspend fun saveTransaction(transaction: Transaction) {}

    private fun isInteger(str: String?) = str?.toIntOrNull()?.let { true } ?: false
}