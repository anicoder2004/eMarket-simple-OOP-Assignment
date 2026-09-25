# MainViewModel.kt

## Purpose
Shared ViewModel (Activity-scoped). Holds UI state (LiveData), exposes actions, handles purchase result events.

## Execution Flow
```
ViewModel Created → init() → observeData()
    → collects Repository Flows (walletBalance, products, transactions)
    → updates internal MutableStateFlows
    → LiveData emits to observing Fragments

Fragment calls depositMoney(amount)
    → viewModelScope.launch → repository.depositMoney()
    → Repository updates DB → Flow emits → ViewModel updates _walletBalance

Fragment calls purchaseProduct(product)
    → viewModelScope.launch → repository.purchaseProduct()
    → on success → _purchaseResult.tryEmit(Success)
    → on failure → determines reason → emits InsufficientBalance/OutOfStock/Failed
```

## Code Walkthrough

```kotlin
class MainViewModel(private val repository: AppRepository) : ViewModel() {

    // STATE - StateFlow → LiveData (lifecycle-aware)
    private val _walletBalance = MutableStateFlow(0.0)
    val walletBalance = _walletBalance.asStateFlow().asLiveData(viewModelScope.coroutineContext)

    private val _products = MutableStateFlow<List<ProductEntity>>(emptyList())
    val products = _products.asStateFlow().asLiveData(viewModelScope.coroutineContext)

    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val transactions = _transactions.asStateFlow().asLiveData(viewModelScope.coroutineContext)

    // EVENTS - SharedFlow (no replay for new observers)
    private val _purchaseResult = MutableSharedFlow<PurchaseResult>(
        extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val purchaseResult = _purchaseResult.asSharedFlow()

    init { observeData() }

    private fun observeData() {
        viewModelScope.launch { repository.walletBalance.collect { _walletBalance.value = it } }
        viewModelScope.launch { repository.products.collect { _products.value = it } }
        viewModelScope.launch { repository.transactions.collect { _transactions.value = it } }
    }

    fun depositMoney(amount: Double) {
        viewModelScope.launch { repository.depositMoney(amount) }
    }

    fun purchaseProduct(product: ProductEntity) {
        viewModelScope.launch {
            val success = repository.purchaseProduct(product)
            if (success) _purchaseResult.tryEmit(PurchaseResult.Success(product.name))
            else {
                val balance = _walletBalance.value
                val prod = _products.value.find { it.id == product.id }
                when {
                    prod == null || prod.stock <= 0 -> _purchaseResult.tryEmit(PurchaseResult.OutOfStock)
                    balance < product.price -> _purchaseResult.tryEmit(PurchaseResult.InsufficientBalance)
                    else -> _purchaseResult.tryEmit(PurchaseResult.Failed)
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
```

## OOP Concepts
- **Constructor Injection**: `private val repository: AppRepository`
- **Encapsulation**: Private `_state` + public `state` (read-only)
- **Sealed Interface**: `PurchaseResult` - exhaustive `when` handling
- **State vs Event**: `StateFlow` for state (replay), `SharedFlow` for events (no replay)
- **Structured Concurrency**: `viewModelScope.launch` - auto-cancelled on clear

## Called By
- DashboardFragment: `depositMoney()`, observes `walletBalance`
- CatalogFragment: `purchaseProduct()`, observes `products`, `walletBalance`, collects `purchaseResult`
- HistoryFragment: observes `transactions`