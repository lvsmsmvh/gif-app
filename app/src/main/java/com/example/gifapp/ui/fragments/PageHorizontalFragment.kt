package com.example.gifapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.gifapp.R
import com.example.gifapp.databinding.FragmentPageHorizontalBinding
import com.example.gifapp.domain.entities.GifPicture
import com.example.gifapp.ui.adapters.gif_picture.GifPictureFullAdapter
import com.example.gifapp.ui.viewmodels.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class PageHorizontalFragment : Fragment() {

    companion object {
        const val KEY_SELECTED_GIF_ID = "KEY_SELECTED_GIF_ID"
    }

    private val viewModel: PageViewModel by activityViewModels()

    private var _binding: FragmentPageHorizontalBinding? = null
    private val binding get() = _binding!!

    private var _gifPictureAdapter: GifPictureFullAdapter? = null
    private val gifPictureAdapter get() = _gifPictureAdapter!!

    private var currentGifPicture: GifPicture? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPageHorizontalBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
    }

    private fun initData() {
        viewModel.page.observe(viewLifecycleOwner) { state ->
            val gifPictures = state.asLoaded()?.result?.gifPictures ?: return@observe
            configureViewPager(gifPictures)
//            viewModel.loadImages(state.result.gifPictures)
        }

        viewModel.localUrls.observe(viewLifecycleOwner) { gifPictureLiveDataMap ->
            if (gifPictureLiveDataMap == null) return@observe

            gifPictureLiveDataMap.forEach { (gifPicture, liveData) ->
                liveData.observe(viewLifecycleOwner) {
                    gifPictureAdapter.addUrl(gifPicture, it)
                }
            }
        }
//        viewModel.localUrlsOld.observe(viewLifecycleOwner) {
//            if (it == null) return@observe
//            CoroutineScope(Dispatchers.Main).launch {
//                gifPictureAdapter.setUrls(requireContext(), it)
//            }
//        }
    }

    private fun configureViewPager(gifPictures: List<GifPicture>) {
        _gifPictureAdapter = GifPictureFullAdapter(
            onClicked = { gifPicture ->
                // todo
            },
        )

        gifPictureAdapter.set(gifPictures)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentGifPicture = gifPictures[position]
                setIndicator(position + 1, gifPictures.size)
            }
        })

        binding.viewPager.offscreenPageLimit = gifPictures.size
        binding.viewPager.adapter = gifPictureAdapter

        // Immediately navigate to previously clicked gif
        val selectedGifPictureId = arguments?.getString(KEY_SELECTED_GIF_ID)
        gifPictures.find { it.id == selectedGifPictureId }?.let { selected ->
            val index = gifPictures.indexOf(selected)
            binding.viewPager.setCurrentItem(index, false)
            setIndicator(index + 1, gifPictures.size)
        }
    }

    private fun setIndicator(current: Int, total: Int) {
        val indicatorText = getString(R.string.page_indicator_ph, current, total)
        binding.tvPageIndicator.text = indicatorText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _gifPictureAdapter = null
        _binding = null
    }
}