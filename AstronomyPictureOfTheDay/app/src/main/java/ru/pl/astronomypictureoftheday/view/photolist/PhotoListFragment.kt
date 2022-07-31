package ru.pl.astronomypictureoftheday.view.photolist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.FragmentPhotoListBinding
import ru.pl.astronomypictureoftheday.view.ViewModelFactory
import ru.pl.astronomypictureoftheday.view.photolist.PhotoListViewModel.Companion.THEME_DARK
import ru.pl.astronomypictureoftheday.view.photolist.PhotoListViewModel.Companion.THEME_LIGHT

private const val TAG = "PhotoListFragment";
const val PREFS_NAME = "PREFS_NAME"

class PhotoListFragment : Fragment() {

    private var _binding: FragmentPhotoListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            getString(R.string.binding_null_error)
        }

    private val photoListViewModel: PhotoListViewModel by viewModels {
        ViewModelFactory(requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE))
    }

/*    private val sharedPrefs by lazy {
        requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }*/


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoListBinding.inflate(inflater, container, false)

        setUpMenu()

        binding.photoGrid.layoutManager = setUpLayoutManager()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressBar.visibility = View.VISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoListViewModel.topPhotos.collect { photoList ->
                    binding.photoGrid.adapter = PhotoListAdapter(photoList.reversed()) {
                        findNavController().navigate(PhotoListFragmentDirections.goToDetails(it))
                    }
                    if (photoList.isNotEmpty()) binding.progressBar.visibility = View.GONE
                    Log.d(TAG, "loaded")
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

    private fun setUpMenu() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
                val switchItem = menu.findItem(R.id.enable_dark_mode)
                switchItem.setActionView(R.layout.switch_dark_mode)

                val switch: SwitchMaterial =
                    menu.findItem(R.id.enable_dark_mode).actionView.findViewById(R.id.switcher)
                switch.isChecked = photoListViewModel.getSavedTheme() == THEME_DARK

                switch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        photoListViewModel.setTheme(AppCompatDelegate.MODE_NIGHT_YES, THEME_DARK)
                    } else {
                        photoListViewModel.setTheme(AppCompatDelegate.MODE_NIGHT_NO, THEME_LIGHT)
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

}