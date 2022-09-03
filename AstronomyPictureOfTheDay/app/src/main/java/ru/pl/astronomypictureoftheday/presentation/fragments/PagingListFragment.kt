package ru.pl.astronomypictureoftheday.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.FragmentPhotoListBinding
import ru.pl.astronomypictureoftheday.presentation.TopPhotoApplication
import ru.pl.astronomypictureoftheday.presentation.adapters.PhotoListPagingAdapter
import ru.pl.astronomypictureoftheday.presentation.adapters.PhotoLoadStateAdapter
import ru.pl.astronomypictureoftheday.presentation.viewModels.PagingListViewModel
import ru.pl.astronomypictureoftheday.presentation.viewModels.PhotoViewModelFactory
import ru.pl.astronomypictureoftheday.utils.DatePickerValidator
import ru.pl.astronomypictureoftheday.utils.findTopNavController
import ru.pl.astronomypictureoftheday.utils.toast
import java.util.*
import javax.inject.Inject

private const val TAG = "PhotoListFragment"

class PagingListFragment : Fragment() {

    private var _binding: FragmentPhotoListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            getString(R.string.binding_null_error)
        }

    private lateinit var materialDatePicker: MaterialDatePicker<Pair<Long, Long>>

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

        collectViewModelState()

        //create material date picker and setup Button
        materialDatePicker = setUpDateRangePickerDialog()
        setupFloatingDateButton()
        setupRecyclerViewWithFloatingButton()
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

    private fun setupRecyclerViewWithFloatingButton() {
        binding.photoGrid.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    binding.selectDateButton.show()
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && binding.selectDateButton.isShown)
                    binding.selectDateButton.hide()
            }
        })
    }

    private fun collectViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                pagingListViewModel.dBPhotosState.collect {
                    pagingAdapter.onChangeFavourites(it.dbPhotoList)
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

    private fun setupFloatingDateButton() {
        binding.selectDateButton.setOnClickListener {
            materialDatePicker.show(childFragmentManager, PICKER_TAG)
            materialDatePicker.addOnPositiveButtonClickListener {
                pagingListViewModel.onDateSelected(it.first to it.second)
            }
        }
        binding.selectDateButton.setOnLongClickListener {
            Log.d(TAG, "long click")
            pagingListViewModel.onDateSelectedReset()
            true
        }
    }

    private fun setUpDateRangePickerDialog(): MaterialDatePicker<Pair<Long, Long>> {
        val dateValidator = DatePickerValidator()
        val constraints = CalendarConstraints.Builder()
            .setStart(dateValidator.minDateLong)
            .setEnd(dateValidator.maxDateLong)
            .setValidator(dateValidator)
            .build()

        val materialDatePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.select_date))
            .setTheme(R.style.date_picker_style)
            .setCalendarConstraints(constraints)
            .build()

        materialDatePicker.addOnPositiveButtonClickListener {
            Log.d(TAG, "${Date(materialDatePicker.selection?.first!!)} : ${Date(materialDatePicker.selection?.second!!)}")
        }

        return materialDatePicker
    }

    companion object {
        private const val PICKER_TAG = "tag"
    }


}