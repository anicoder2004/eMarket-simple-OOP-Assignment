# WalletEntity.kt

## Purpose
Room entity for wallet table. Single row (id=1) for single-user app.

## Code Walkthrough

```kotlin
@Entity(tableName = "wallet")
data class WalletEntity(
    @PrimaryKey val id: Int = 1,  // Fixed ID - single wallet
    val balance: Double
)
```

## OOP Concepts
- **Data Class**: Compiler generates `copy()`, `equals()`, `hashCode()`, `toString()`
- **Immutable Update**: `wallet.copy(balance = newBalance)` creates new instance

## Used By
- WalletDao: queries/updates this table
- AppRepository: reads/writes via DAO
- AppDatabase: seeded with ৳1000