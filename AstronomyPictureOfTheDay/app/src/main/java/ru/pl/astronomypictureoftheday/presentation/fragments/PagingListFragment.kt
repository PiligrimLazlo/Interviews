package ru.pl.astronomypictureoftheday.presentation.fragments

import android.content.Context
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
import ru.pl.astronomypictureoftheday.presentation.TopPhotoApplication
import ru.pl.astronomypictureoftheday.presentation.adapters.PhotoListPagingAdapter
import ru.pl.astronomypictureoftheday.presentation.adapters.PhotoLoadStateAdapter
import ru.pl.astronomypictureoftheday.presentation.viewModels.PagingListViewModel
import ru.pl.astronomypictureoftheday.presentation.viewModels.PhotoViewModelFactory
import ru.pl.astronomypictureoftheday.utils.findTopNavController
import ru.pl.astronomypictureoftheday.utils.toast
import javax.inject.Inject

private const val TAG = "PhotoListFragment";

class PagingListFragment : Fragment() {

    private var _binding: FragmentPhotoListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            getString(R.string.binding_null_error)
        }

    @Inject
    lateinit var photoViewModelFactory: PhotoViewModelFactory
    private val pagingListViewModel: PagingListViewModel by viewModels {
        photoViewModelFactory
    }
    private lateinit var pagingAdapter: PhotoListPagingAdapter

    private val component by lazy {
        (requireActivity().application as TopPhotoApplication)
            .component
            /*.fragmentComponentFactory()
            .create(null)*/
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

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
        pagingAdapter = setupPagingAdapter()
        //footer progress + error msg + retry btn
        setUpAdapterHeaderAndFooter(pagingAdapter)
        //paging collect data from viewModel
        collectAdapterData(pagingAdapter)
        //paging center retry button listener
        binding.retryButton.setOnClickListener { pagingAdapter.retry() }
        //paging center progress and btn visibility
        collectAdapterLoadState(pagingAdapter)

        //это нужно для синхр с БД
        collectDbPhotoList()
    }

    private fun setupPagingAdapter(): PhotoListPagingAdapter {
        return PhotoListPagingAdapter({
            findTopNavController().navigate(TabsFragmentDirections.goToDetails(it, it.title))
        }, {
            if (!it.isFavourite) {
                toast("\"${it.title}\" ${getString(R.string.added_to_favourites)}")
            } else {
                toast("\"${it.title}\" ${getString(R.string.removed_from_favourites)}")
            }
            pagingListViewModel.onSaveFavouriteButtonPressed(it, requireContext().filesDir)
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
                pagingListViewModel.photoEntityItemsFromPaging.collectLatest {
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

    private fun collectDbPhotoList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pagingListViewModel.dbPhotoListState.collect {
                    pagingAdapter.onChangeFavourites(it)
                }
            }
        }
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