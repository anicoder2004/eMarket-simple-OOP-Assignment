# ProductDao.kt

## Purpose
DAO for products table. Reactive list, single lookup, updates, bulk insert, count.

## Code Walkthrough

```kotlin
@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id ASC")
    fun getAllProductsFlow(): Flow<List<ProductEntity>>  // Reactive catalog

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Int): ProductEntity?  // Parameter binding

    @Update
    suspend fun updateProduct(product: ProductEntity)  // Updates all non-PK fields

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)  // Bulk insert for seeding

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getProductCount(): Int  // Detects first run
}
```

## Used By
- AppRepository: exposes `products` Flow, calls `getProductById()`, `updateProduct()`, `insertProducts()`, `getProductCount()`
- AppDatabase: abstract method `productDao()`