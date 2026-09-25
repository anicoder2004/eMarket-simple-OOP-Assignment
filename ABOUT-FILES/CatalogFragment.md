# CatalogFragment.kt

## Purpose
Product catalog screen. RecyclerView with products, toolbar shows balance, handles purchase clicks.

## Execution Flow
```
Fragment Created → by viewModels { factory } → shared MainViewModel
    → onViewCreated → setupRecyclerView() + observeViewModel()

setupRecyclerView():
    binding.productsRecyclerView.layoutManager = LinearLayoutManager
    binding.productsRecyclerView.adapter = ProductAdapter { product →
        viewModel.purchaseProduct(product)  // Click → ViewModel
    }

observeViewModel():
    viewModel.products.observe → adapter.submitList() + empty state toggle
    viewModel.walletBalance.observe → binding.catalogToolbar.subtitle = "Balance: ৳..."
    
    lifecycleScope.launch { viewModel.purchaseResult.collect { result →
        when (result) {
            Success → Toast "Purchased X successfully!"
            InsufficientBalance → Toast "Insufficient wallet balance..."
            OutOfStock → Toast "Item is out of stock!"
            Failed → Toast "Purchase failed..."
        }
    }}
```

## Code Walkthrough

```kotlin
class CatalogFragment : Fragment() {
    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels { factory }
    private val adapter = ProductAdapter { product → viewModel.purchaseProduct(product) }

    override fun onViewCreated(...) {
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() { ... }  // LinearLayoutManager + adapter

    private fun observeViewModel() {
        // Products list
        viewModel.products.observe(viewLifecycleOwner) { list → adapter.submitList(list) ... }
        
        // Balance in toolbar
        viewModel.walletBalance.observe(viewLifecycleOwner) { balance →
            binding.catalogToolbar.subtitle = "Balance: ৳ %.2f".format(balance)
        }
        
        // Purchase results (SharedFlow events)
        lifecycleScope.launch {
            viewModel.purchaseResult.collect { when (it) { ... } }
        }
    }
}
```

## OOP Concepts
- **Shared ViewModel**: Same MainViewModel instance as DashboardFragment
- **Adapter Pattern**: RecyclerView.Adapter with click lambda (dependency inversion)
- **Sealed Interface Handling**: Exhaustive `when` on `PurchaseResult`
- **SharedFlow Collection**: `collect` in coroutine for one-time events

## Called By
- MainActivity: hosted in NavHostFragment (now startDestination)
- Gets ViewModel from: ViewModelFactory → EMarketApplication.getRepository()
- Calls: MainViewModel.purchaseProduct()
- Observes: MainViewModel.products, walletBalance
- Collects: MainViewModel.purchaseResult
- Creates: ProductAdapter