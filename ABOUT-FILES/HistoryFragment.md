# HistoryFragment.kt

## Purpose
Transaction history screen. Read-only RecyclerView showing deposits/purchases.

## Execution Flow
```
Fragment Created → by viewModels { factory } → shared MainViewModel
    → onViewCreated → setupRecyclerView() + observeViewModel()

setupRecyclerView():
    binding.transactionsRecyclerView.layoutManager = LinearLayoutManager
    binding.transactionsRecyclerView.adapter = TransactionAdapter()

observeViewModel():
    viewModel.transactions.observe(viewLifecycleOwner) { list →
        adapter.submitList(list)
        emptyView/recyclerView visibility toggle
    }
```

## Code Walkthrough

```kotlin
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels { factory }
    private val adapter = TransactionAdapter()

    override fun onViewCreated(...) {
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() { ... }

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner) { list →
            adapter.submitList(list)
            binding.emptyView.visibility = if (list.isEmpty()) VISIBLE else GONE
            binding.transactionsRecyclerView.visibility = if (list.isEmpty()) GONE else VISIBLE
        }
    }
}
```

## Called By
- MainActivity: hosted in NavHostFragment
- Gets ViewModel from: ViewModelFactory → EMarketApplication.getRepository()
- Observes: MainViewModel.transactions (LiveData)
- Creates: TransactionAdapter