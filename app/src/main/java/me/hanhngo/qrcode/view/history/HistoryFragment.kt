package me.hanhngo.qrcode.view.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.databinding.library.baseAdapters.BR
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import me.hanhngo.qrcode.adapter.ItemClickListener
import me.hanhngo.qrcode.databinding.FragmentHistoryBinding
import me.hanhngo.qrcode.domain.BarcodeItem
import me.hanhngo.qrcode.domain.schema.Email
import me.hanhngo.qrcode.domain.schema.Other
import me.hanhngo.qrcode.domain.schema.Url
import me.hanhngo.qrcode.util.parseSchema

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding: FragmentHistoryBinding get() = _binding!!

    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDataBindingVariable()
        bindUI()
        setupClickListener()
    }

    private fun setupDataBindingVariable() {
        binding.lifecycleOwner = this.viewLifecycleOwner
        binding.setVariable(BR.viewModel, viewModel)

        val listener = ItemClickListener { barcodeItem ->
            onBarcodeItemClick(barcodeItem)
        }
        binding.setVariable(BR.listener, listener)
    }

    private fun bindUI() {
        binding.barcodeHistoryRcv.apply {
            val dividerItemDecoration = DividerItemDecoration(this.context, RecyclerView.VERTICAL)
            this.addItemDecoration(dividerItemDecoration)
        }

        binding.searchBarSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.searchBarcodeList(p0.toString())
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                viewModel.searchBarcodeList(p0.toString())
                return true
            }
        })
    }

    private fun setupClickListener() {
        binding.searchBarSv.setOnClickListener {
            binding.searchBarSv.isIconified = false
        }
    }

    private fun onBarcodeItemClick(barcodeItem: BarcodeItem) {
        val schema = parseSchema(barcodeItem.rawValue)
        val format = barcodeItem.format
        when (schema) {
            is Email -> NavHostFragment.findNavController(this).navigate(
                HistoryFragmentDirections.actionHistoryFragmentToEmailFragment(schema, format)
            )

            is Url -> NavHostFragment.findNavController(this).navigate(
                HistoryFragmentDirections.actionHistoryFragmentToUrlFragment(schema, format)
            )
            else -> NavHostFragment.findNavController(this).navigate(
                HistoryFragmentDirections.actionHistoryFragmentToOtherFragment(
                    schema as Other,
                    format
                )
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
