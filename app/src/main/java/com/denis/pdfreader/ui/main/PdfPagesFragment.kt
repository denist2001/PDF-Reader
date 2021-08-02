package com.denis.pdfreader.ui.main

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denis.pdfreader.PageProviderViewModel
import com.denis.pdfreader.databinding.MainFragmentBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PdfPagesFragment : Fragment() {
    private lateinit var binding: MainFragmentBinding

    private val mainAdapter = PageDataAdapter()

    companion object {
        private const val DOCUMENT_URI_ARGUMENT =
            "com.denis.pdfreader.args.DOCUMENT_URI_ARGUMENT"

        fun newInstance(documentUri: Uri) = PdfPagesFragment().apply {
            arguments = Bundle().apply {
                putString(DOCUMENT_URI_ARGUMENT, documentUri.toString())
            }
        }
    }

    private lateinit var viewModel: PageProviderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PageProviderViewModel::class.java)
        mainAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        with(binding.pdfPagerRv) {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = mainAdapter
        }
        val uriArgument = arguments?.getString(DOCUMENT_URI_ARGUMENT) ?: return

        lifecycleScope.launch {
            mainAdapter.loadStateFlow.collectLatest {
                Log.d("Adapter state flow", it.toString())
            }
        }
        lifecycleScope.launch {
            viewModel.bitmapsFlow.collectLatest { bitmap ->
                //that solved the problem
                lifecycleScope.launch {
                    mainAdapter.submitData(bitmap)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.loadFirstPage(requireContext(), uriArgument)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.close()
    }
}