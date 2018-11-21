package com.dmide.revolutassignment.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.dmide.revolutassignment.R
import com.dmide.revolutassignment.app.CurrenciesApplication
import com.dmide.revolutassignment.databinding.ActivityCurrenciesBinding
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class CurrenciesActivity : AppCompatActivity() {
    @Inject
    lateinit var currenciesViewModelFactory: CurrenciesViewModelFactory

    private lateinit var binding: ActivityCurrenciesBinding
    private lateinit var viewModel: CurrenciesViewModel
    private val listAdapter by lazy { CurrenciesAdapter(this, viewModel) }
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as CurrenciesApplication).component.inject(this)
        viewModel = ViewModelProviders.of(this, currenciesViewModelFactory).get(CurrenciesViewModel::class.java)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_currencies)
        binding.currencyList.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = listAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(rv: RecyclerView, state: Int) = viewModel.onNewScrollState(state)
            })
            viewModel.onNewScrollState(RecyclerView.SCROLL_STATE_IDLE)
        }

        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel

        viewModel.errorMessageLiveData.observe(this, Observer { message ->
            if (message != null) showErrorSnack(message) else hideErrorSnack()
        })
    }

    private fun showErrorSnack(message: CurrenciesViewModel.ErrorMessage) {
        if (snackbar != null) return
        
        snackbar = Snackbar.make(binding.root, message.text, Snackbar.LENGTH_INDEFINITE)
            .apply { show() }
    }

    private fun hideErrorSnack() {
        snackbar?.dismiss()
        snackbar = null
    }
}
