# DashboardFragment.kt

## Purpose
Deposit screen. Shows balance, handles deposit input/validation, calls ViewModel.

## Execution Flow
```
Fragment Created → by viewModels { factory } → gets shared MainViewModel
    → onViewCreated → observeViewModel() + setupListeners()

observeViewModel():
    viewModel.walletBalance.observe(viewLifecycleOwner) { balance →
        binding.walletBalance.text = "৳ %.2f".format(balance)
    }

setupListeners() - Deposit Button Click:
    1. Get amount from EditText
    2. Validate: not blank, valid Double, > 0, ≤ 50000
    3. Show specific toasts for each error case
    4. viewModel.depositMoney(amount)
    5. Clear input: binding.amountInput.setText("")
    6. Toast "Money deposited successfully!" (in lifecycleScope)
```

## Code Walkthrough

```kotlin
class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels { 
        ViewModelFactory((requireActivity().application as EMarketApplication).getRepository()) 
    }

    override fun onCreateView(...) = FragmentDashboardBinding.inflate(...).root

    override fun onViewCreated(...) {
        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() { ... }  // LiveData → UI

    private fun setupListeners() {
        binding.depositButton.setOnClickListener { ... }  // Validation + ViewModel call
    }

    override fun onDestroyView() { _binding = null }
}
```

## OOP Concepts
- **ViewBinding Pattern**: `_binding`/`binding`
- **Property Delegation**: `by viewModels { factory }` - Activity-scoped ViewModel
- **Lifecycle-Aware Observation**: `observe(viewLifecycleOwner)`
- **Coroutine Scope**: `lifecycleScope.launch` - auto-cancelled

## Called By
- MainActivity: hosted in NavHostFragment (nav_graph startDestination was dashboardFragment, now catalogFragment)
- Gets ViewModel from: ViewModelFactory → EMarketApplication.getRepository()
- Calls: MainViewModel.depositMoney()
- Observes: MainViewModel.walletBalance