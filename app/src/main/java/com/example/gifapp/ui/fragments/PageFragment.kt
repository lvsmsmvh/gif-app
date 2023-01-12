package com.example.gifapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gifapp.R
import com.example.gifapp.databinding.FragmentPageBinding
import com.example.gifapp.domain.exceptions.LoadException
import com.example.gifapp.domain.exceptions.NothingFoundException
import com.example.gifapp.ui.adapters.gif_picture.GifPictureSmallAdapter
import com.example.gifapp.ui.viewmodels.*
import com.example.gifapp.utils.logDebug
import com.example.gifapp.utils.simpleNavigate
import com.example.gifapp.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PageFragment : Fragment() {

    private val viewModel: PageViewModel by activityViewModels()

    private var _binding: FragmentPageBinding? = null
    private val binding get() = _binding!!

    private var _gifPictureAdapter: GifPictureSmallAdapter? = null
    private val gifPictureAdapter get() = _gifPictureAdapter!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initData()

        viewModel.loadFirstPage()
    }

    private fun initView() {
        binding.btnReload.setOnClickListener {
            viewModel.loadFirstPage()
        }

        binding.btnOffline.setOnClickListener {
            viewModel.goOfflineMode()
        }

        binding.recycleViewGifs.layoutManager = GridLayoutManager(
            requireContext(), 4, RecyclerView.VERTICAL, false
        )

        binding.recycleViewGifs.adapter = GifPictureSmallAdapter(
            onClicked = { gifPicture ->
                requireActivity().simpleNavigate(
                    PageHorizontalFragment::class.java,
                    Bundle().apply {
                        putString(PageHorizontalFragment.KEY_SELECTED_GIF_ID, gifPicture.id)
                    })
            },
        ).also { _gifPictureAdapter = it }
    }

    private fun initData() {
        viewModel.localUrls.observe(viewLifecycleOwner) { gifPictureLiveDataMap ->
            if (gifPictureLiveDataMap == null) return@observe
            gifPictureLiveDataMap.forEach { (gifPicture, liveData) ->
                liveData.observe(viewLifecycleOwner) {
                    gifPictureAdapter.addUrl(gifPicture, it)
                }
            }
        }

        viewModel.page.observe(viewLifecycleOwner) { state ->
            updateLayout(state)
            when (state) {
                is LoadingState.Loading -> {
                    logDebug("LoadingState.Loading")
                }
                is LoadingState.Failed -> {
                    logDebug("LoadingState.Failed")
                    when (val exception = state.throwable) {
                        is LoadException -> {
                            binding.tvError.setText(R.string.error_loading)
                            binding.tvErrorSmall.text = exception.message
                            logDebug("LoadingState.Failed: ${exception.message}")
                        }
                        is NothingFoundException -> {
                            binding.tvError.setText(R.string.nothing_found)
                            binding.tvErrorSmall.setText(R.string.this_page_is_empty)
                            logDebug("LoadingState.Failed: ${getString(R.string.this_page_is_empty)}")
                            binding.btnReload.isVisible = false
                        }
                    }
                }
                is LoadingState.Loaded -> {
                    logDebug("LoadingState.Loaded: ${state.result.gifPictures.size}")
                    gifPictureAdapter.set(state.result.gifPictures)
                    binding.pagination.isVisible = true
                    viewModel.loadImages(state.result.gifPictures)
                }
            }
        }
    }

    private fun updateLayout(loadingState: LoadingState<Any>) {
        binding.layoutLoading.isVisible = loadingState.isLoading()
        binding.layoutFailed.isVisible = loadingState.isFailed()
        binding.layoutLoaded.isVisible = loadingState.isLoaded()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _gifPictureAdapter = null
        _binding = null
    }
}