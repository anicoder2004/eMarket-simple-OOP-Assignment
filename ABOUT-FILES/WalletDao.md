# WalletDao.kt

## Purpose
DAO for wallet table. Reactive Flow + one-shot suspend queries.

## Code Walkthrough

```kotlin
@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet WHERE id = 1")
    fun getWalletFlow(): Flow<WalletEntity?>  // Reactive - emits on change

    @Query("SELECT * FROM wallet WHERE id = 1")
    suspend fun getWalletDirect(): WalletEntity?  // One-shot for mutations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWallet(wallet: WalletEntity)  // Upsert (fixed ID=1)
}
```

## Flow vs Suspend
| Method | Returns | Use Case |
|--------|---------|----------|
| `getWalletFlow()` | `Flow<WalletEntity?>` | UI observation (Dashboard balance) |
| `getWalletDirect()` | `WalletEntity?` | Repository mutations (deposit/purchase) |

## Used By
- AppRepository: exposes `walletBalance` Flow, calls `getWalletDirect()`/`updateWallet()`
- AppDatabase: abstract method `walletDao()`