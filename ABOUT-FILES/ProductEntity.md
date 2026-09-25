# ProductEntity.kt

## Purpose
Room entity for products table. Auto-generated ID, mutable stock.

## Code Walkthrough

```kotlin
@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // SQLite AUTOINCREMENT
    val name: String,
    val price: Double,
    var stock: Int,                                    // Mutable - changes on purchase
    val iconResName: String = "ic_product_placeholder" // Default icon
)
```

## OOP Concepts
- **Auto-generated Key**: `autoGenerate = true` - Room assigns ID on insert
- **Mutable Field**: `var stock` allows `product.copy(stock = newStock)`

## Used By
- ProductDao: all product operations
- AppRepository: reads list, updates stock on purchase
- AppDatabase: seeded with 15 products
- ProductAdapter: binds to RecyclerView