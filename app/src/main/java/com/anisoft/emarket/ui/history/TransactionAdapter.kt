package com.anisoft.emarket.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anisoft.emarket.R
import com.anisoft.emarket.data.local.entity.TransactionEntity
import com.anisoft.emarket.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactions: List<TransactionEntity> = emptyList()

    inner class TransactionViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        val isDeposit = transaction.type == "DEPOSIT"

        holder.binding.transactionType.text = if (isDeposit) "Deposit" else "Purchase"
        holder.binding.transactionTitle.text = transaction.title
        holder.binding.transactionAmount.text = String.format(
            if (isDeposit) "+%.2f" else "-%.2f",
            transaction.amount
        )
        holder.binding.transactionAmount.setTextColor(
            if (isDeposit) holder.itemView.context.getColor(R.color.green) else holder.itemView.context.getColor(R.color.red)
        )
        holder.binding.transactionIcon.setImageResource(
            if (isDeposit) R.drawable.ic_deposit else R.drawable.ic_purchase
        )
        holder.binding.transactionDate.text = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
            .format(Date(transaction.timestamp))
    }

    override fun getItemCount(): Int = transactions.size

    fun submitList(newTransactions: List<TransactionEntity>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}