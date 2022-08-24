package ru.pl.astronomypictureoftheday.view.photolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.FragmentPhotoListBinding
import ru.pl.astronomypictureoftheday.utils.findTopNavController
import ru.pl.astronomypictureoftheday.utils.toast
import ru.pl.astronomypictureoftheday.view.adapters.PhotoListPagingAdapter
import ru.pl.astronomypictureoftheday.view.adapters.PhotoLoadStateAdapter
import ru.pl.astronomypictureoftheday.view.bottomnav.TabsFragmentDirections
import kotlin.properties.Delegates

private const val TAG = "PhotoListFragment";

class PhotoListFragment : Fragment() {

    private var _binding: FragmentPhotoListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            getString(R.string.binding_null_error)
        }

    private val photoListViewModel: PhotoListViewModel by viewModels()
    private var currentTheme by Delegates.notNull<Int>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoListBinding.inflate(inflater, container, false)
        binding.photoGrid.layoutManager = setUpLayoutManager()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //paging adapter
        val pagingAdapter = setupPagingAdapter()
        //footer progress + error msg + retry btn
        setUpAdapterHeaderAndFooter(pagingAdapter)
        //paging collect data from viewModel
        collectAdapterData(pagingAdapter)
        //paging center retry button listener
        binding.retryButton.setOnClickListener { pagingAdapter.retry() }
        //paging center progress and btn visibility
        collectAdapterLoadState(pagingAdapter)
    }

    private fun setupPagingAdapter(): PhotoListPagingAdapter {
        return PhotoListPagingAdapter({
            findTopNavController().navigate(TabsFragmentDirections.goToDetails(it, it.title))
        }, {
            if (it.isFavourite) {
                toast("\"${it.title}\" ${getString(R.string.added_to_favourites)}")
            } else {
                toast("\"${it.title}\" ${getString(R.string.removed_from_favourites)}")
            }
            photoListViewModel.onSaveFavouriteButtonPressed(it, requireContext().filesDir)
        })
    }

    private fun collectAdapterLoadState(pagingAdapter: PhotoListPagingAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            pagingAdapter.loadStateFlow.collect { loadState ->
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                binding.retryButton.isVisible = loadState.source.refresh is LoadState.Error
            }
        }
    }

    private fun collectAdapterData(pagingAdapter: PhotoListPagingAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoListViewModel.photoEntityItemsFromPaging.collectLatest {
                    pagingAdapter.submitData(it)
                }
            }
        }
    }

    private fun setUpAdapterHeaderAndFooter(pagingAdapter: PhotoListPagingAdapter) {
        binding.photoGrid.adapter = pagingAdapter.withLoadStateHeaderAndFooter(
            header = PhotoLoadStateAdapter { pagingAdapter.retry() },
            footer = PhotoLoadStateAdapter { pagingAdapter.retry() }
        )
    }




    private fun setUpLayoutManager(): GridLayoutManager {
        val layoutManger = GridLayoutManager(context, 2)
        layoutManger.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 2 else 1
            }
        }
        return layoutManger
    }



}