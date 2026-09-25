# TransactionDao.kt

## Purpose
DAO for transactions table. Append-only audit log.

## Code Walkthrough

```kotlin
@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<TransactionEntity>>  // Newest first

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)  // Log deposit/purchase
}
```

## Design
- **No Update/Delete**: Transactions are immutable audit records
- **DESC Order**: Newest transactions first for history screen

## Used By
- AppRepository: exposes `transactions` Flow, calls `insertTransaction()`
- AppDatabase: abstract method `transactionDao()`