# EMarketApplication.kt

## Purpose
Application class - app entry point. Initializes Room database and Repository as singletons.

## Execution Flow
```
App Start → Android creates EMarketApplication → onCreate()
    → AppDatabase.getDatabase() → singleton DB
    → AppRepository(db) → singleton Repository
    → getRepository() available globally
```

## Code Walkthrough

```kotlin
class EMarketApplication : Application() {
    private var database: AppDatabase? = null
    private var repository: AppRepository? = null

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getDatabase(this)  // Singleton DB via Room
        repository = AppRepository(database!!)    // Repository with DB reference
    }

    fun getRepository(): AppRepository = repository!!
}
```

## OOP Concepts
- **Singleton Pattern**: `AppDatabase.getDatabase()` returns same instance
- **Manual Dependency Injection**: Repository receives DB in constructor
- **Encapsulation**: `private` fields, public getter

## Called By
- Fragments/ViewModels: `(application as EMarketApplication).getRepository()`