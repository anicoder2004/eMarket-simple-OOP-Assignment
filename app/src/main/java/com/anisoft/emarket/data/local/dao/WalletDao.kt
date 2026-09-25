package com.anisoft.emarket.data.local.dao

import androidx.room.*
import com.anisoft.emarket.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet WHERE id = 1")
    fun getWalletFlow(): Flow<WalletEntity?>

    @Query("SELECT * FROM wallet WHERE id = 1")
    suspend fun getWalletDirect(): WalletEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWallet(wallet: WalletEntity)
}