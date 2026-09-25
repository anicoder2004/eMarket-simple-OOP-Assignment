# ViewModelFactory.kt

## Purpose
Factory to create MainViewModel with Repository dependency (since MainViewModel has no zero-arg constructor).

## Execution Flow
```
Fragment: by viewModels { ViewModelFactory(repository) }
    → Android calls factory.create(MainViewModel::class.java)
    → returns MainViewModel(repository)
```

## Code Walkthrough

```kotlin
class ViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

## OOP Concepts
- **Factory Pattern**: Centralizes ViewModel creation with dependencies
- **Generics**: `<T : ViewModel>` for type safety
- **Interface Implementation**: `ViewModelProvider.Factory`

## Called By
- All Fragments: `by viewModels { ViewModelFactory((application as EMarketApplication).getRepository()) }`