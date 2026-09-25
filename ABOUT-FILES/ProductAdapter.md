# ProductAdapter.kt

## Purpose
RecyclerView.Adapter for catalog products. Binds data, manages stock badge, handles buy clicks via lambda.

## Execution Flow
```
CatalogFragment creates adapter:
    ProductAdapter { product → viewModel.purchaseProduct(product) }

RecyclerView requests views:
    onCreateViewHolder → inflate item_product.xml → ProductViewHolder(binding)

RecyclerView binds data:
    onBindViewHolder(holder, position):
        1. product = products[position]
        2. Set name, price, stock text
        3. isInStock = stock > 0
        4. stockBadge: text + background (green/red)
        5. buyNowButton: enabled/alpha based on stock
        6. buyNowButton.setOnClickListener { onBuyClick(product) }

CatalogFragment calls adapter.submitList(newProducts) when products LiveData emits
```

## Code Walkthrough

```kotlin
class ProductAdapter(
    private val onBuyClick: (ProductEntity) -> Unit  // Click callback
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products: List<ProductEntity> = emptyList()

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent, viewType) = 
        ProductViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder, position) {
        val product = products[position]
        holder.binding.productName.text = product.name
        holder.binding.productPrice.text = "৳ %.2f".format(product.price)
        holder.binding.productStock.text = "In Stock: ${product.stock}"

        val isInStock = product.stock > 0
        holder.binding.stockBadge.text = if (isInStock) "In Stock" else "Out of Stock"
        holder.binding.stockBadge.setBackgroundResource(
            if (isInStock) R.drawable.stock_badge_background_green else R.drawable.stock_badge_background_red
        )
        holder.binding.buyNowButton.isEnabled = isInStock
        holder.binding.buyNowButton.alpha = if (isInStock) 1.0f else 0.5f

        holder.binding.buyNowButton.setOnClickListener { onBuyClick(product) }
    }

    override fun getItemCount() = products.size

    fun submitList(newProducts: List<ProductEntity>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
```

## OOP Concepts
- **Adapter Pattern**: Bridges List data → RecyclerView UI
- **ViewHolder Pattern**: View reuse for performance
- **Dependency Inversion**: `onBuyClick` lambda - adapter doesn't know purchase logic
- **ViewBinding**: `ItemProductBinding` per ViewHolder

## Called By
- CatalogFragment: creates adapter with lambda, calls `submitList()`
- RecyclerView: calls `onCreateViewHolder`, `onBindViewHolder`, `getItemCount`