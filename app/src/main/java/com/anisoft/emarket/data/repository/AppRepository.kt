package com.anisoft.emarket.data.repository

import com.anisoft.emarket.data.local.AppDatabase
import com.anisoft.emarket.data.local.dao.ProductDao
import com.anisoft.emarket.data.local.dao.TransactionDao
import com.anisoft.emarket.data.local.dao.WalletDao
import com.anisoft.emarket.data.local.entity.ProductEntity
import com.anisoft.emarket.data.local.entity.TransactionEntity
import com.anisoft.emarket.data.local.entity.WalletEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppRepository(private val db: AppDatabase) {
    private val walletDao: WalletDao = db.walletDao()
    private val productDao: ProductDao = db.productDao()
    private val transactionDao: TransactionDao = db.transactionDao()

    val walletBalance: Flow<Double> = walletDao.getWalletFlow().map { it?.balance ?: 0.0 }
    val products: Flow<List<ProductEntity>> = productDao.getAllProductsFlow()
    val transactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactionsFlow()

    suspend fun depositMoney(amount: Double): Boolean = withContext(Dispatchers.IO) {
        val wallet = walletDao.getWalletDirect() ?: return@withContext false
        if (amount <= 0 || amount > 50000) return@withContext false
        val newBalance = wallet.balance + amount
        walletDao.updateWallet(wallet.copy(balance = newBalance))
        transactionDao.insertTransaction(TransactionEntity(
            type = "DEPOSIT",
            amount = amount,
            title = "Deposit Money"
        ))
        true
    }

    suspend fun purchaseProduct(product: ProductEntity): Boolean = withContext(Dispatchers.IO) {
        val wallet = walletDao.getWalletDirect() ?: return@withContext false
        if (product.stock <= 0) return@withContext false
        if (wallet.balance < product.price) return@withContext false

        val newBalance = wallet.balance - product.price
        walletDao.updateWallet(wallet.copy(balance = newBalance))

        val updatedProduct = product.copy(stock = product.stock - 1)
        productDao.updateProduct(updatedProduct)

        transactionDao.insertTransaction(TransactionEntity(
            type = "PURCHASE",
            amount = product.price,
            title = product.name
        ))
        true
    }
}