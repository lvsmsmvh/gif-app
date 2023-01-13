package com.example.gifapp.ui.fragments

import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gifapp.R
import com.example.gifapp.databinding.FragmentPageBinding
import com.example.gifapp.domain.entities.Page
import com.example.gifapp.domain.exceptions.LoadException
import com.example.gifapp.domain.exceptions.NothingFoundException
import com.example.gifapp.ui.adapters.gif_picture.GifPictureSmallAdapter
import com.example.gifapp.ui.viewmodels.*
import com.example.gifapp.utils.setCustomClickable
import com.example.gifapp.utils.simpleNavigate
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PageFragment : Fragment() {

    private val viewModel: PageViewModel by activityViewModels()

    private var _textWatcher: TextWatcher? = null

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

        initTabLayout()
        initTextWatcher()
        initView()
        initData()

        viewModel.loadFirstPageIfNothingLoaded()
    }

    private fun initTabLayout() {
        val tabLayout = binding.tabLayout

        val tabOnline = tabLayout.newTab()
        tabLayout.addTab(tabOnline)
        tabOnline.text = getString(R.string.online)

        val tabSaved = tabLayout.newTab()
        tabLayout.addTab(tabSaved)
        tabSaved.text = getString(R.string.saved)

        tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position == tabOnline.position) {
                    true -> viewModel.goOnlineMode()
                    false -> viewModel.goOfflineMode()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun initTextWatcher() {
        _textWatcher = binding.searchView.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                val textAfter = text?.toString() ?: return@addTextChangedListener
                viewModel.search(textAfter)
            }
        )
    }

    private fun initView() {
        binding.btnPrev.setOnClickListener { viewModel.loadPreviousPage() }
        binding.btnNext.setOnClickListener { viewModel.loadNextPage() }
        binding.btnReload.setOnClickListener {
            viewModel.updateFailedPage()
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

        viewModel.pagination.observe(viewLifecycleOwner) { pagination ->
            binding.pagination.setCustomClickable(!pagination.hideButtons)

            pagination.isPrevEnabled.asNewOrNull()?.value
                ?.let { binding.btnPrev.setCustomClickable(it) }

            pagination.isNextEnabled.asNewOrNull()?.value
                ?.let { binding.btnNext.setCustomClickable(it) }

            pagination.indicator.asNewOrNull()?.value
                ?.let { binding.tvPageIndicator.text = it }
        }

        viewModel.page.observe(viewLifecycleOwner) { state ->
            updateLayout(state)
            when (state) {
                is LoadingState.Loading -> {
                }
                is LoadingState.Failed -> {
                    when (val exception = state.throwable) {
                        is LoadException -> {
                            binding.tvError.setText(R.string.error_loading)
                            binding.tvErrorSmall.text = exception.message
                        }
                        is NothingFoundException -> {
                            binding.tvError.setText(R.string.nothing_found)
                            binding.tvErrorSmall.setText(R.string.this_page_is_empty)
                            binding.btnReload.isVisible = false
                        }
                    }
                }
                is LoadingState.Loaded -> {
                    gifPictureAdapter.set(state.result.gifPictures)
                    binding.pagination.isVisible = true
                    viewModel.loadImages(state.result.gifPictures)
                }
            }
        }
    }

    private fun updateLayout(loadingState: LoadingState<Page>) {
        binding.layoutLoading.isVisible = loadingState.isLoading()
        binding.layoutFailed.isVisible = loadingState.isFailed()
        binding.layoutLoaded.isVisible = loadingState.isLoaded()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _gifPictureAdapter = null
        _binding?.searchView?.removeTextChangedListener(_textWatcher)
        _binding = null
    }
}