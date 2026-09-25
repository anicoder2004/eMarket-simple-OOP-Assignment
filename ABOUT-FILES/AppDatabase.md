# AppDatabase.kt

## Purpose
Room database class. Defines schema (entities), provides DAO accessors, implements singleton, seeds initial data.

## Execution Flow
```
EMarketApplication.onCreate() → AppDatabase.getDatabase(context)
    → singleton check (double-checked locking)
    → Room.databaseBuilder().addCallback(SeedCallback).build()
    → on first run: SeedCallback.onCreate() → seedDatabase()
        → inserts 15 products if empty
        → inserts wallet with ৳1000 if null
```

## Code Walkthrough

```kotlin
@Database(entities = [WalletEntity::class, ProductEntity::class, TransactionEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "emarket_database"
                ).addCallback(SeedCallback).build()
                INSTANCE = instance
                instance
            }
        }

        private object SeedCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                CoroutineScope(Dispatchers.IO).launch {
                    val instance = INSTANCE ?: return@launch
                    seedDatabase(instance)
                }
            }
        }

        private suspend fun seedDatabase(db: AppDatabase) { ... } // inserts 15 products + wallet
    }
}
```

## OOP Concepts
- **Abstract Class**: Room generates implementation
- **Singleton Pattern**: `@Volatile` + `synchronized` double-checked locking
- **Callback Pattern**: `SeedCallback` runs once on DB creation
- **Annotation Processing**: `@Database`, `@Entity`, `@Dao` drive compile-time code gen

## Called By
- EMarketApplication: `AppDatabase.getDatabase(this)`
- Provides DAOs to: AppRepository