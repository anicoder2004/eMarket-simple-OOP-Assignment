package com.anisoft.emarket.ui.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.anisoft.emarket.R
import com.anisoft.emarket.data.local.entity.ProductEntity
import com.anisoft.emarket.databinding.FragmentCatalogBinding
import com.anisoft.emarket.ui.main.MainViewModel
import com.anisoft.emarket.ui.main.ViewModelFactory
import com.anisoft.emarket.EMarketApplication
import kotlinx.coroutines.launch

class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels { ViewModelFactory((requireActivity().application as EMarketApplication).getRepository()) }
    private val adapter = ProductAdapter { product ->
        viewModel.purchaseProduct(product)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.productsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.productsRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.products.observe(viewLifecycleOwner) { productList ->
            adapter.submitList(productList)
            binding.emptyView.visibility = if (productList.isEmpty()) View.VISIBLE else View.GONE
            binding.productsRecyclerView.visibility = if (productList.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.walletBalance.observe(viewLifecycleOwner) { balance ->
            binding.catalogToolbar.subtitle = String.format("Balance: ৳ %.2f", balance)
        }

        lifecycleScope.launch {
            viewModel.purchaseResult.collect { result ->
                when (result) {
                    is MainViewModel.PurchaseResult.Success -> {
                        Toast.makeText(requireContext(), "Purchased ${result.productName} successfully!", Toast.LENGTH_SHORT).show()
                    }
                    is MainViewModel.PurchaseResult.InsufficientBalance -> {
                        Toast.makeText(requireContext(), getString(R.string.insufficient_balance), Toast.LENGTH_LONG).show()
                    }
                    is MainViewModel.PurchaseResult.OutOfStock -> {
                        Toast.makeText(requireContext(), getString(R.string.out_of_stock), Toast.LENGTH_SHORT).show()
                    }
                    is MainViewModel.PurchaseResult.Failed -> {
                        Toast.makeText(requireContext(), getString(R.string.purchase_failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}