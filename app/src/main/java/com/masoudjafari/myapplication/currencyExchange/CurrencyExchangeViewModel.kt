package com.masoudjafari.myapplication.currencyExchange

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.lifecycle.*
import com.masoudjafari.myapplication.Event
import com.masoudjafari.myapplication.R
import com.masoudjafari.myapplication.data.Balance
import com.masoudjafari.myapplication.data.CurrencyExchangeRate
import com.masoudjafari.myapplication.data.Result
import com.masoudjafari.myapplication.data.Transaction
import com.masoudjafari.myapplication.data.source.CurrencyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


/*
    most values and functions are private,
    This protects the app data inside the ViewModel from unwanted and unsafe changes by external classes,
    but it allows external callers to safely access its value using public values.
*/
@HiltViewModel
class CurrencyExchangeViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    private val _updateCurrenciesFromRemote = MutableLiveData(true)
    private val _updateBalances = MutableLiveData(false)

    private var soledCurrencyName = ""
    private var receivedCurrencyName = ""

    private val _currencyRates: MutableLiveData<List<CurrencyExchangeRate>> =
        getCurrencyRates(_updateCurrenciesFromRemote.value!!)
    val currencyRates: MutableLiveData<List<CurrencyExchangeRate>> = _currencyRates
    val sellCurrencyValue = MutableLiveData<String>()

    private val _balances: LiveData<List<Balance>> = _updateBalances.switchMap {
        val result = MutableLiveData<List<Balance>>()
        viewModelScope.launch {
            val balances = currencyRepository.getBalances()
            if (balances is Result.Success) {
                result.value = balances.data!!
                if (soledCurrencyName.isEmpty())
                    soledCurrencyName = balances.data[0].currency
                if (receivedCurrencyName.isEmpty())
                    receivedCurrencyName = balances.data[0].currency
            } else {
                result.value = emptyList()
                showSnackbarMessage(R.string.connection_error)
            }
        }
        result
    }
    val balances: LiveData<List<Balance>> = _balances

    private fun getCurrencyRates(updateFromRemote: Boolean): MutableLiveData<List<CurrencyExchangeRate>> {
        val result = MutableLiveData<List<CurrencyExchangeRate>>()
        viewModelScope.launch {
            val databaseRates = currencyRepository.getCurrencyRates(false)
            if (databaseRates is Result.Success)
                result.value = databaseRates.data!!
            else
                result.value = emptyList()

            if (updateFromRemote || result.value?.isEmpty() == true) {
                val currencyRates = currencyRepository.getCurrencyRates(true)
                if (currencyRates is Result.Success)
                    result.value = currencyRates.data!!
                else {
                    result.value = emptyList()
                    showSnackbarMessage(R.string.connection_error)
                }
            }
        }
        return result
    }

    // set CurrencyExchange event
    fun submitCurrencyExchange() {
        calculateExchangeRate()
    }

    // show a message to user
    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText
    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    // show a convert message to user
    private val _convertSnackbarText = MutableLiveData<String>()
    val convertSnackbarText: LiveData<String> = _convertSnackbarText
    private fun showConvertSnackbarMessage(message: String) {
        _convertSnackbarText.value = message
    }

    private fun syncCurrencyExchangeRates() {
        viewModelScope.launch {
            while (true) {
                getCurrencyRates(true)
                delay(5000)
            }
        }
    }

    private fun refreshBalances() {
        _updateBalances.value = true
    }

    private fun getDateTime(): String {
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        return sdf.format(Date())
    }

    private fun calculateExchangeRate() {
        viewModelScope.launch {
            val sellCurrencyRate: Double
            val receiveCurrencyRate: Double
            val sellCurrencyBalanceValue: Double
            val sellCurrencyValue = sellCurrencyValue.value?.toDouble()
            var commission = 0.0
            var transactionsCount = 0

            // check sell Currency value is not empty
            if (sellCurrencyValue == null || sellCurrencyValue == 0.0) {
                _snackbarText.value = Event(R.string.enterSellValue)
                return@launch
            }

            // check sell and receive currency is different
            if (soledCurrencyName == receivedCurrencyName) {
                _snackbarText.value = Event(R.string.sameCurrency)
                return@launch
            }

            val sellCurrencyBalance = currencyRepository.getBalance(soledCurrencyName)
            if (sellCurrencyBalance is Result.Success) {
                if (sellCurrencyBalance.data == null) {
                    _snackbarText.value = Event(R.string.not_enough_balance)
                    return@launch
                } else
                    sellCurrencyBalanceValue = sellCurrencyBalance.data.balance
            } else {
                _snackbarText.value = Event(R.string.calculation_error)
                return@launch
            }

            // check sell Currency value is more than balance
            if (sellCurrencyValue > sellCurrencyBalanceValue) {
                _snackbarText.value = Event(R.string.not_enough_balance)
                return@launch
            }

            val sellCurrency = currencyRepository.getCurrencyRate(soledCurrencyName)
            if (sellCurrency is Result.Success)
                sellCurrencyRate = sellCurrency.data.rate
            else {
                _snackbarText.value = Event(R.string.calculation_error)
                return@launch
            }

            val receiveCurrency = currencyRepository.getCurrencyRate(receivedCurrencyName)
            if (receiveCurrency is Result.Success)
                receiveCurrencyRate = receiveCurrency.data.rate
            else {
                _snackbarText.value = Event(R.string.calculation_error)
                return@launch
            }

            val oldBalance = currencyRepository.getBalance(receivedCurrencyName)
            var oldBalanceValue = 0.0
            if (oldBalance is Result.Success && oldBalance.data != null)
                oldBalanceValue = oldBalance.data.balance

            val transactions = currencyRepository.getTransactions()
            if (transactions is Result.Success)
                transactionsCount = transactions.data.size

            if (transactionsCount >= 5)
                commission = sellCurrencyValue * 0.7

            val receiveBalance =
                (sellCurrencyValue - commission) * sellCurrencyRate / receiveCurrencyRate
            val receivedBalance = Balance(receivedCurrencyName, receiveBalance + oldBalanceValue)
            val soledBalance =
                Balance(soledCurrencyName, sellCurrencyBalanceValue - sellCurrencyValue)

            val transaction = Transaction(
                soledCurrencyName,
                receivedCurrencyName,
                sellCurrencyValue,
                commission,
                getDateTime()
            )

            currencyRepository.saveBalance(receivedBalance)
            currencyRepository.updateBalance(soledBalance)
            currencyRepository.saveTransaction(transaction)
            refreshBalances()

            val message = if (transactionsCount <= 5)
                "You have converted $sellCurrencyValue $soledCurrencyName to" +
                        " $receiveBalance $receivedCurrencyName."
            else
                "You have converted $sellCurrencyValue $soledCurrencyName to" +
                        " $receiveBalance $receivedCurrencyName. Commission Fee -$commission $soledCurrencyName"
            showConvertSnackbarMessage(message)
        }
    }

    val sellSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            soledCurrencyName = parent?.getItemAtPosition(position) as String
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    val receiveSelectListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            receivedCurrencyName = parent?.getItemAtPosition(position) as String
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    init {
        syncCurrencyExchangeRates()
        val x = null
        val l = listOf(x)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("app:sourceData")
        fun setSourceData(spinner: Spinner, sourceList: List<CurrencyExchangeRate>?) {
            if (sourceList != null) {
                val items = ArrayList<String>()
                items.addAll(sourceList.map { it.currency })
                val spinnerAdapter =
                    ArrayAdapter(
                        spinner.context,
                        android.R.layout.simple_spinner_dropdown_item,
                        items
                    )
                spinner.adapter = spinnerAdapter
            }
        }
    }
}