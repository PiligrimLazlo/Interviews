package ru.pl.astronomypictureoftheday.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.FragmentFavouritesBinding
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.presentation.TopPhotoApplication
import ru.pl.astronomypictureoftheday.presentation.adapters.PhotoListFavouritesAdapter
import ru.pl.astronomypictureoftheday.presentation.viewmodels.FavouritesListViewModel
import ru.pl.astronomypictureoftheday.presentation.viewmodels.PhotoViewModelFactory
import ru.pl.astronomypictureoftheday.utils.findTopNavController
import ru.pl.astronomypictureoftheday.utils.toast
import javax.inject.Inject

class FavouritesListFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            getString(R.string.binding_null_error)
        }

    @Inject
    lateinit var photoViewModelFactory: PhotoViewModelFactory
    private val favouritesListViewModel: FavouritesListViewModel by viewModels {
        photoViewModelFactory
    }

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
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        binding.photoGridFavourites.layoutManager = GridLayoutManager(context, 2)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PhotoListFavouritesAdapter({
            findTopNavController().navigate(TabsFragmentDirections.goToDetails(it, it.title))
        }, {
            if (!it.isFavourite) {
                toast("\"${it.title}\" ${getString(R.string.added_to_favourites)}")
            } else {
                toast("\"${it.title}\" ${getString(R.string.removed_from_favourites)}")
            }
            favouritesListViewModel.onSaveFavouriteButtonPressed(it, requireContext().filesDir)
        })
        binding.photoGridFavourites.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                favouritesListViewModel.photosEntity.collect {
                    updateUi(it)
                    val list = it
                    adapter.submitList(list)
                }
            }
        }
    }

    private fun updateUi(listFavourites: List<PhotoEntity>) {
        if (listFavourites.isEmpty()) {
            binding.emptyFavouritesListText.visibility = View.VISIBLE
        } else {
            binding.emptyFavouritesListText.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}