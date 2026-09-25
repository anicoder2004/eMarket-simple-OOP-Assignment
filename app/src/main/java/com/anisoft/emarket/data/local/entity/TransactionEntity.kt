package com.anisoft.emarket.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val amount: Double,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)