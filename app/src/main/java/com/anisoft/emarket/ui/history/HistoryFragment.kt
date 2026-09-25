package com.anisoft.emarket.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.anisoft.emarket.R
import com.anisoft.emarket.databinding.FragmentHistoryBinding
import com.anisoft.emarket.ui.main.MainViewModel
import com.anisoft.emarket.ui.main.ViewModelFactory
import com.anisoft.emarket.EMarketApplication

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels { ViewModelFactory((requireActivity().application as EMarketApplication).getRepository()) }
    private val adapter = TransactionAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.transactionsRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner) { transactionList ->
            adapter.submitList(transactionList)
            binding.emptyView.visibility = if (transactionList.isEmpty()) View.VISIBLE else View.GONE
            binding.transactionsRecyclerView.visibility = if (transactionList.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}