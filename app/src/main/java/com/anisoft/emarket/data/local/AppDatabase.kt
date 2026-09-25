package com.anisoft.emarket.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.anisoft.emarket.data.local.dao.ProductDao
import com.anisoft.emarket.data.local.dao.TransactionDao
import com.anisoft.emarket.data.local.dao.WalletDao
import com.anisoft.emarket.data.local.entity.ProductEntity
import com.anisoft.emarket.data.local.entity.TransactionEntity
import com.anisoft.emarket.data.local.entity.WalletEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [WalletEntity::class, ProductEntity::class, TransactionEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun walletDao(): WalletDao
    abstract fun productDao(): ProductDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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
            override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val instance = INSTANCE ?: return@launch
                    seedDatabase(instance)
                }
            }
        }

        private suspend fun seedDatabase(db: AppDatabase) {
            val productCount = db.productDao().getProductCount()
            if (productCount == 0) {
                val products = listOf(
                    ProductEntity(name = "Wireless Earbuds", price = 2500.0, stock = 10),
                    ProductEntity(name = "Mechanical Keyboard", price = 4200.0, stock = 5),
                    ProductEntity(name = "Gaming Mouse", price = 1800.0, stock = 8),
                    ProductEntity(name = "USB-C Fast Charger", price = 950.0, stock = 15),
                    ProductEntity(name = "Smartwatch Series 5", price = 6500.0, stock = 3),
                    ProductEntity(name = "Laptop Stand Aluminum", price = 3200.0, stock = 7),
                    ProductEntity(name = "4K Webcam", price = 5500.0, stock = 4),
                    ProductEntity(name = "RGB Mechanical Keyboard", price = 7800.0, stock = 6),
                    ProductEntity(name = "Wireless Charging Pad", price = 1200.0, stock = 12),
                    ProductEntity(name = "Portable SSD 1TB", price = 9500.0, stock = 5),
                    ProductEntity(name = "Noise Cancelling Headphones", price = 12500.0, stock = 3),
                    ProductEntity(name = "UltraWide Monitor 34\"", price = 42000.0, stock = 2),
                    ProductEntity(name = "Mechanical Switch Tester", price = 850.0, stock = 20),
                    ProductEntity(name = "Cable Management Kit", price = 450.0, stock = 25),
                    ProductEntity(name = "Desk LED Light Bar", price = 1800.0, stock = 10)
                )
                db.productDao().insertProducts(products)
            }

            val wallet = db.walletDao().getWalletDirect()
            if (wallet == null) {
                db.walletDao().updateWallet(WalletEntity(id = 1, balance = 1000.0))
            }
        }
    }
}