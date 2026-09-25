# MainActivity.kt

## Purpose
Single Activity host. Sets up BottomNavigationView + NavHostFragment. Connects bottom nav to navigation graph.

## Execution Flow
```
App Launch → MainActivity.onCreate()
    → inflate activity_main.xml (BottomNavigationView + NavHostFragment)
    → setupNavigation()
        → find NavHostFragment by ID
        → get navController
        → binding.bottomNavigation.setupWithNavController(navController)
```

## Code Walkthrough

```kotlin
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)  // Auto-syncs bottom nav with nav graph
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null  // Prevent memory leak
    }
}
```

## OOP Concepts
- **ViewBinding Pattern**: `_binding`/`binding` for safe view access
- **Delegation**: `setupWithNavController()` handles all nav click logic

## Called By
- Android OS (launcher Activity)
- Hosts: DashboardFragment, CatalogFragment, HistoryFragment via NavHostFragment