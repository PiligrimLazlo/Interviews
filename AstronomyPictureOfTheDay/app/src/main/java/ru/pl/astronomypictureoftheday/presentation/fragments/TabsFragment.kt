package ru.pl.astronomypictureoftheday.presentation.fragments

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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.FragmentBottomNavBinding
import ru.pl.astronomypictureoftheday.presentation.TopPhotoApplication
import ru.pl.astronomypictureoftheday.presentation.viewModels.PhotoViewModelFactory
import ru.pl.astronomypictureoftheday.presentation.viewModels.TabsViewModel
import ru.pl.astronomypictureoftheday.presentation.viewModels.TabsViewModel.Companion.THEME_DARK
import ru.pl.astronomypictureoftheday.presentation.viewModels.TabsViewModel.Companion.THEME_LIGHT
import javax.inject.Inject
import kotlin.properties.Delegates

private const val TAG = "TabsFragmentTag"

class TabsFragment : Fragment() {

    private var _binding: FragmentBottomNavBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            getString(R.string.binding_null_error)
        }

    @Inject
    lateinit var photoViewModelFactory: PhotoViewModelFactory
    private val tabsViewModel: TabsViewModel by viewModels {
        photoViewModelFactory
    }
    private var currentTheme by Delegates.notNull<Int>()
    private var checkedAutoWallpapersSet by Delegates.notNull<Boolean>()

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
        _binding = FragmentBottomNavBinding.inflate(inflater, container, false)

        //connect nav component to the bottom navigation view
        val navHost = childFragmentManager.findFragmentById(R.id.tabs_container) as NavHostFragment
        val navController = navHost.navController
        NavigationUI.setupWithNavController(binding.bottomNavView, navController)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //state (theme and marked photos list)
        collectUiState()
        //app bar menu
        setUpMenu()
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                tabsViewModel.tabsState.collect { state ->
                    currentTheme = state.theme
                    val themeMode =
                        if (currentTheme == THEME_LIGHT)
                            AppCompatDelegate.MODE_NIGHT_NO
                        else
                            AppCompatDelegate.MODE_NIGHT_YES
                    AppCompatDelegate.setDefaultNightMode(themeMode)

                    checkedAutoWallpapersSet = state.isAutoWallpapersEnabled
                    Log.d(TAG, "tabs state changed $checkedAutoWallpapersSet")
                }
            }
        }
    }

    private fun setUpMenu() {
        val menuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_main, menu)
                //это здесь, т.к. отдельный свитч в тулбаре
                val switchItem = menu.findItem(R.id.enable_dark_mode)
                switchItem.setActionView(R.layout.switch_dark_mode)
                val switch: SwitchMaterial = switchItem.actionView.findViewById(R.id.switcher)
                switch.isChecked = currentTheme == THEME_DARK

                switch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        tabsViewModel.setTheme(THEME_DARK)
                    } else {
                        tabsViewModel.setTheme(THEME_LIGHT)
                    }
                }

                val checkIsAutoWallpEnable = menu.findItem(R.id.switch_set_wallpaper)
                checkIsAutoWallpEnable.isChecked = checkedAutoWallpapersSet

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.switch_set_wallpaper -> {
                        menuItem.isChecked = !menuItem.isChecked
                        tabsViewModel.setAutoWallpapersEnabled(menuItem.isChecked)
                    }
                }

                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.STARTED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}