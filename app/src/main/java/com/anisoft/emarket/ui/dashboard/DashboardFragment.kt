package com.anisoft.emarket.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.anisoft.emarket.R
import com.anisoft.emarket.databinding.FragmentDashboardBinding
import com.anisoft.emarket.ui.main.MainViewModel
import com.anisoft.emarket.ui.main.ViewModelFactory
import com.anisoft.emarket.EMarketApplication
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels { ViewModelFactory((requireActivity().application as EMarketApplication).getRepository()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupListeners()
    }

    private fun observeViewModel() {
        viewModel.walletBalance.observe(viewLifecycleOwner) { balance ->
            binding.walletBalance.text = String.format("৳ %.2f", balance)
        }
    }

    private fun setupListeners() {
        binding.depositButton.setOnClickListener {
            val amountText = binding.amountInput.text.toString()
            if (amountText.isBlank()) {
                Toast.makeText(requireContext(), getString(R.string.invalid_amount), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(requireContext(), getString(R.string.invalid_amount), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amount > 50000) {
                Toast.makeText(requireContext(), getString(R.string.amount_exceeds_limit), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            viewModel.depositMoney(amount)
            binding.amountInput.setText("")

            lifecycleScope.launch {
                Toast.makeText(requireContext(), getString(R.string.deposit_success), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}