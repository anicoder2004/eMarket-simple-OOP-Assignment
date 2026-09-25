# TransactionAdapter.kt

## Purpose
RecyclerView.Adapter for transaction history. Read-only, formats type/amount/date with colors/icons.

## Execution Flow
```
HistoryFragment creates adapter: TransactionAdapter()

RecyclerView binds:
    onBindViewHolder(holder, position):
        transaction = transactions[position]
        isDeposit = type == "DEPOSIT"

        holder.binding.transactionTitle.text = transaction.title          // Product name / "Deposit Money"
        holder.binding.transactionType.text = if (isDeposit) "Deposit" else "Purchase"

        holder.binding.transactionAmount.text = 
            if (isDeposit) "+%.2f" else "-%.2f".format(transaction.amount)
        holder.binding.transactionAmount.setTextColor(
            if (isDeposit) Color.GREEN else Color.RED
        )
        holder.binding.transactionIcon.setImageResource(
            if (isDeposit) R.drawable.ic_deposit else R.drawable.ic_purchase
        )
        holder.binding.transactionDate.text = 
            SimpleDateFormat("MMM dd, yyyy hh:mm a").format(Date(transaction.timestamp))

HistoryFragment calls adapter.submitList() when transactions LiveData emits
```

## Code Walkthrough

```kotlin
class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactions: List<TransactionEntity> = emptyList()

    inner class TransactionViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent, viewType) = 
        TransactionViewHolder(ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder, position) {
        val transaction = transactions[position]
        val isDeposit = transaction.type == "DEPOSIT"

        holder.binding.transactionTitle.text = transaction.title
        holder.binding.transactionType.text = if (isDeposit) "Deposit" else "Purchase"
        holder.binding.transactionAmount.text = String.format(if (isDeposit) "+%.2f" else "-%.2f", transaction.amount)
        holder.binding.transactionAmount.setTextColor(
            if (isDeposit) holder.itemView.context.getColor(R.color.green) else holder.itemView.context.getColor(R.color.red)
        )
        holder.binding.transactionIcon.setImageResource(
            if (isDeposit) R.drawable.ic_deposit else R.drawable.ic_purchase
        )
        holder.binding.transactionDate.text = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
            .format(Date(transaction.timestamp))
    }

    override fun getItemCount() = transactions.size

    fun submitList(newTransactions: List<TransactionEntity>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
```

## Display Layout (item_transaction.xml)
```
[Icon]  Title (product name / "Deposit Money")     +৳ 2,500.00 (green) / -৳ 950.00 (red)
        Type: "Deposit" / "Purchase"                      Sep 25, 2026 10:30 AM
────────────────────────────────────────────────────────────────────────
```

## Called By
- HistoryFragment: creates adapter, calls `submitList()`
- RecyclerView: standard adapter methods