package com.masoudjafari.myapplication.currencyExchange

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.masoudjafari.myapplication.databinding.FragmentCurrencyExchangeBinding
import com.masoudjafari.myapplication.util.setupSnackbar
import com.masoudjafari.myapplication.util.setupConvertSnackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CurrencyExchangeFragment : Fragment() {

    private val viewModel by viewModels<CurrencyExchangeViewModel>()
    private lateinit var listAdapter: BalanceAdapter
    private lateinit var binding: FragmentCurrencyExchangeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCurrencyExchangeBinding.inflate(layoutInflater, container, false).apply {
            viewmodel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar()
        setupConvertSnackbar()
        setupListLayoutManager()
        setupListAdapter()
    }

    private fun setupListLayoutManager() {
        binding.balancesList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
    }

    private fun setupListAdapter() {
        val viewModel = binding.viewmodel
        if (viewModel != null) {
            listAdapter = BalanceAdapter(viewModel)
            binding.balancesList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupSnackbar() {
        view?.setupSnackbar(viewLifecycleOwner, viewModel.snackbarText, Snackbar.LENGTH_LONG)
    }

    private fun setupConvertSnackbar() {
        view?.setupConvertSnackbar(viewLifecycleOwner, viewModel.convertSnackbarText, Snackbar.LENGTH_INDEFINITE)
    }

}