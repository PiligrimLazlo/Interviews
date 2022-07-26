package ru.pl.astronomypictureoftheday.presentation.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.domain.PhotoEntity
import ru.pl.astronomypictureoftheday.databinding.FragmentPhotoDetailsBinding
import ru.pl.astronomypictureoftheday.presentation.TopPhotoApplication
import ru.pl.astronomypictureoftheday.presentation.viewmodels.PhotoDetailsState
import ru.pl.astronomypictureoftheday.presentation.viewmodels.PhotoDetailsViewModel
import ru.pl.astronomypictureoftheday.presentation.viewmodels.PhotoViewModelFactory
import ru.pl.astronomypictureoftheday.utils.toast
import java.io.File
import javax.inject.Inject

private const val TAG = "PhotoDetailsFragment"

class PhotoDetailsFragment : Fragment() {

    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            getString(R.string.binding_null_error)
        }

    @Inject
    lateinit var photoViewModelFactory: PhotoViewModelFactory
    private val photoDetailsViewModel: PhotoDetailsViewModel by viewModels {
        photoViewModelFactory
    }
    private val args: PhotoDetailsFragmentArgs by navArgs()
    private lateinit var photoEntity: PhotoEntity

    //ask write in memory permission
    private val requestReadWritePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        for (permission in permissions.entries) {
            val permissionName = permission.key
            val granted = permission.value

            if (!granted) {
                toast("$permissionName ${getString(R.string.permission_not_granted_error)}")
                return@registerForActivityResult
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            photoDetailsViewModel.saveImageToPictureFolder(requireContext().filesDir)
        }
    }

    private val component by lazy {
        (requireActivity().application as TopPhotoApplication)
            .component
            .fragmentComponentFactory()
            .create(photoEntity)
    }

    override fun onAttach(context: Context) {
        photoEntity = args.photoEntity
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setAppBarTitle(favouritePhoto.title)
        collectUiState()
        //init photo and text load
        initialLoadDataIntoScreen()

        binding.saveToGalleryBtn.setOnClickListener {
            if (isWriteInMemoryPermissionGranted()) {
                viewLifecycleOwner.lifecycleScope.launch {
                    photoDetailsViewModel.saveImageToPictureFolder(requireContext().filesDir)
                }
            } else {
                requestWriteInMemoryPermission()
            }
        }
        binding.setWallpapersBtn.setOnClickListener {
            showWallpaperDialog { position ->
                viewLifecycleOwner.lifecycleScope.launch {
                    photoDetailsViewModel.setWallpapers(position, requireContext().filesDir)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoDetailsViewModel.detailsState.collect { state ->
                    updateUi(state)
                }
            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun initialLoadDataIntoScreen() {

        binding.apply {
            descriptionDetail.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD

            val path = photoEntity.cachePhotoPath
            Glide.with(root.context)
                .load(
                    if (File(path).exists())
                        BitmapFactory.decodeFile(path)
                    else
                        photoEntity.imageUrl
                )
                .placeholder(R.drawable.placeholder_400x400)
                .error(R.drawable.error_400x400)
                .listener(GlideRequestListener {
                    saveToGalleryBtn.isEnabled = false
                    setWallpapersBtn.isEnabled = false
                })
                .into(imageDetail)

            dateDetail.text = photoEntity.formattedDate
        }
    }


    private fun requestWriteInMemoryPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showRationaleDialog(
                getString(R.string.error),
                getString(R.string.permission_not_granted_error)
            )
        } else {
            requestReadWritePermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun isWriteInMemoryPermissionGranted(): Boolean =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED


    private fun showRationaleDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
            .setMessage(message)
            .setTitle(title)
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.ok)
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showWallpaperDialog(clickListener: (Int) -> Unit) {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
            .setTitle(R.string.set_wallpapers_text)
            .setCancelable(true)
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setItems(R.array.choices) { _, which ->
                clickListener(which)
            }
            .create()
            .show()
    }


    private fun updateUi(state: PhotoDetailsState) {
        binding.apply {
            if (state.isSettingWallpaper) {
                setWallpapersBtn.visibility = View.INVISIBLE
                progressBarWallpapers.visibility = View.VISIBLE
            } else {
                setWallpapersBtn.visibility = View.VISIBLE
                progressBarWallpapers.visibility = View.INVISIBLE
            }
            if (state.isSavingPhoto) {
                saveToGalleryBtn.visibility = View.INVISIBLE
                progressBarSave.visibility = View.VISIBLE
            } else {
                saveToGalleryBtn.visibility = View.VISIBLE
                progressBarSave.visibility = View.INVISIBLE
            }

            state.userMessage?.let { toast(it) }
            photoDetailsViewModel.userMessageShown()


            if (state.translatedDescription != null) {
                binding.descriptionDetail.text = state.translatedDescription
            } else {
                binding.descriptionDetail.text = photoEntity.explanation
            }

        }
    }

}

private class GlideRequestListener(private val callback: () -> Unit) : RequestListener<Drawable> {
    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Drawable>?,
        isFirstResource: Boolean
    ): Boolean {
        callback.invoke()
        return false
    }

    override fun onResourceReady(
        resource: Drawable?,
        model: Any?,
        target: Target<Drawable>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        return false
    }

}