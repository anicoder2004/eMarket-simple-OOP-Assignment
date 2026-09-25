# TransactionEntity.kt

## Purpose
Room entity for transactions table (audit log). Append-only.

## Code Walkthrough

```kotlin
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,        // "DEPOSIT" or "PURCHASE"
    val amount: Double,
    val title: String,       // Product name or "Deposit Money"
    val timestamp: Long = System.currentTimeMillis()  // Auto-set on creation
)
```

## OOP Concepts
- **Default Parameter**: `timestamp = System.currentTimeMillis()` - never forget to set
- **Audit Log Pattern**: Immutable records, only INSERT

## Used By
- TransactionDao: insert + query all
- AppRepository: inserts on deposit/purchase
- TransactionAdapter: binds to RecyclerView in HistoryFragment