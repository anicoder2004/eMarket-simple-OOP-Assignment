package com.anisoft.emarket.ui.catalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anisoft.emarket.R
import com.anisoft.emarket.data.local.entity.ProductEntity
import com.anisoft.emarket.databinding.ItemProductBinding

class ProductAdapter(
    private val onBuyClick: (ProductEntity) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var products: List<ProductEntity> = emptyList()

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.productName.text = product.name
        holder.binding.productPrice.text = String.format("৳ %.2f", product.price)
        holder.binding.productStock.text = "In Stock: ${product.stock}"

        val isInStock = product.stock > 0
        holder.binding.stockBadge.text = if (isInStock) "In Stock" else "Out of Stock"
        holder.binding.stockBadge.setBackgroundResource(
            if (isInStock) R.drawable.stock_badge_background_green else R.drawable.stock_badge_background_red
        )
        holder.binding.buyNowButton.isEnabled = isInStock
        holder.binding.buyNowButton.alpha = if (isInStock) 1.0f else 0.5f

        holder.binding.buyNowButton.setOnClickListener {
            onBuyClick(product)
        }
    }

    override fun getItemCount(): Int = products.size

    fun submitList(newProducts: List<ProductEntity>) {
        products = newProducts
        notifyDataSetChanged()
    }
}