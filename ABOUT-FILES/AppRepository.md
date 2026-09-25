# AppRepository.kt

## Purpose
Single data source. Exposes reactive Flows for UI, provides suspend functions for mutations. Coordinates Wallet/Product/Transaction DAOs.

## Execution Flow
```
ViewModel.observeData() → collects walletBalance/products/transactions Flows
    → Repository exposes DAO Flows directly (products, transactions)
    → walletBalance: walletDao.getWalletFlow().map { it?.balance ?: 0.0 }

ViewModel.depositMoney(amount) → repository.depositMoney(amount)
    → withContext(Dispatchers.IO)
    → walletDao.getWalletDirect() → validate amount ≤ 50000
    → walletDao.updateWallet(wallet.copy(balance = newBalance))
    → transactionDao.insertTransaction(DEPOSIT)

ViewModel.purchaseProduct(product) → repository.purchaseProduct(product)
    → withContext(Dispatchers.IO)
    → walletDao.getWalletDirect() → validate stock > 0, balance ≥ price
    → walletDao.updateWallet(wallet.copy(balance = newBalance))
    → productDao.updateProduct(product.copy(stock = stock - 1))
    → transactionDao.insertTransaction(PURCHASE)
```

## Code Walkthrough

```kotlin
class AppRepository(private val db: AppDatabase) {
    private val walletDao = db.walletDao()
    private val productDao = db.productDao()
    private val transactionDao = db.transactionDao()

    // REACTIVE STREAMS (Flow) - UI observes these
    val walletBalance: Flow<Double> = walletDao.getWalletFlow().map { it?.balance ?: 0.0 }
    val products: Flow<List<ProductEntity>> = productDao.getAllProductsFlow()
    val transactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactionsFlow()

    // MUTATIONS (suspend) - called from ViewModel
    suspend fun depositMoney(amount: Double): Boolean = withContext(Dispatchers.IO) { ... }
    suspend fun purchaseProduct(product: ProductEntity): Boolean = withContext(Dispatchers.IO) { ... }
}
```

## OOP Concepts
- **Repository Pattern**: Single data access point, hides DAO complexity
- **Constructor Injection**: `private val db: AppDatabase`
- **Flow Transformation**: `.map { it?.balance ?: 0.0 }`
- **Data Class Copy**: `wallet.copy(balance = newBalance)` - immutable update
- **Coroutines**: `suspend` + `withContext(Dispatchers.IO)` for background work

## Called By
- MainViewModel: observes all 3 Flows, calls `depositMoney()`, `purchaseProduct()`
- Calls: WalletDao, ProductDao, TransactionDao