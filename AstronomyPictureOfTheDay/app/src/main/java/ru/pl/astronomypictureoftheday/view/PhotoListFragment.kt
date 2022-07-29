package ru.pl.astronomypictureoftheday.view

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
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.FragmentPhotoListBinding
import kotlin.properties.Delegates

private const val TAG = "PhotoListFragment";

class PhotoListFragment : Fragment() {

    private var _binding: FragmentPhotoListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val photoListViewModel: PhotoListViewModel by viewModels()

    private val sharedPrefs by lazy {
        requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    private var currentTheme by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentTheme = getSavedTheme()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoListBinding.inflate(inflater, container, false)
        val layoutManger = GridLayoutManager(context, 2)
        layoutManger.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 2 else 1
            }
        }

        Log.d(TAG, "on create view")

        setUpMenu()

        binding.photoGrid.layoutManager = layoutManger
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoListViewModel.topPhotos.collect { photoList ->
                    binding.photoGrid.adapter = PhotoListAdapter(photoList.reversed())
                    //Log.d(TAG, "$photoList")
                }
            }
        }
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
                switch.isChecked = currentTheme == THEME_DARK

                switch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        setTheme(AppCompatDelegate.MODE_NIGHT_YES, THEME_DARK)
                    } else {
                        setTheme(AppCompatDelegate.MODE_NIGHT_NO, THEME_LIGHT)
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    private fun setTheme(themeMode: Int, prefsMode: Int) {
        AppCompatDelegate.setDefaultNightMode(themeMode)
        saveTheme(prefsMode)
    }


    private fun getSavedTheme() = sharedPrefs.getInt(KEY_THEME, THEME_LIGHT)
    private fun saveTheme(theme: Int) = sharedPrefs.edit().putInt(KEY_THEME, theme).apply()

    companion object {
        const val PREFS_NAME = "PREFS_NAME"

        const val KEY_THEME = "prefs.theme"
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
    }

}

