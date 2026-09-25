package com.anisoft.emarket.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asLiveData
import com.anisoft.emarket.data.repository.AppRepository
import com.anisoft.emarket.data.local.entity.ProductEntity
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: AppRepository) : ViewModel() {

    private val _walletBalance = MutableStateFlow(0.0)
    val walletBalance = _walletBalance.asStateFlow().asLiveData(viewModelScope.coroutineContext)

    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products = _products.asStateFlow().asLiveData(viewModelScope.coroutineContext)

    private val _transactions = MutableStateFlow<List<com.anisoft.emarket.data.local.entity.TransactionEntity>>(emptyList())
    val transactions = _transactions.asStateFlow().asLiveData(viewModelScope.coroutineContext)

    private val _purchaseResult = MutableSharedFlow<PurchaseResult>(extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val purchaseResult = _purchaseResult.asSharedFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.walletBalance.collect { balance ->
                _walletBalance.value = balance
            }
        }
        viewModelScope.launch {
            repository.products.collect { productList ->
                _products.value = productList
            }
        }
        viewModelScope.launch {
            repository.transactions.collect { transactionList ->
                _transactions.value = transactionList
            }
        }
    }

    fun depositMoney(amount: Double) {
        viewModelScope.launch {
            val success = repository.depositMoney(amount)
            if (!success) {
                // Handle error - could add error state flow
            }
        }
    }

    fun purchaseProduct(product: ProductEntity) {
        viewModelScope.launch {
            val success = repository.purchaseProduct(product)
            if (success) {
                _purchaseResult.tryEmit(PurchaseResult.Success(product.name))
            } else {
                // Need to determine the failure reason - check balance and stock
                val currentBalance = _walletBalance.value
                val productInList = _products.value.find { it.id == product.id }
                if (productInList == null || productInList.stock <= 0) {
                    _purchaseResult.tryEmit(PurchaseResult.OutOfStock)
                } else if (currentBalance < product.price) {
                    _purchaseResult.tryEmit(PurchaseResult.InsufficientBalance)
                } else {
                    _purchaseResult.tryEmit(PurchaseResult.Failed)
                }
            }
        }
    }

    sealed interface PurchaseResult {
        data class Success(val productName: String) : PurchaseResult
        object InsufficientBalance : PurchaseResult
        object OutOfStock : PurchaseResult
        object Failed : PurchaseResult
    }
}