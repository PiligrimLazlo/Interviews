package ru.pl.astronomypictureoftheday.view.photodetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.FragmentPhotoDetailsBinding
import ru.pl.astronomypictureoftheday.model.FavouritePhoto
import ru.pl.astronomypictureoftheday.utils.ImageManager
import ru.pl.astronomypictureoftheday.utils.setAppBarTitle
import ru.pl.astronomypictureoftheday.utils.toDefaultFormattedDate
import ru.pl.astronomypictureoftheday.utils.toast

class PhotoDetailsFragment : Fragment() {

    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            getString(R.string.binding_null_error)
        }

    private val photoDetailsViewModel: PhotoDetailsViewModel by viewModels()
    private val args: PhotoDetailsFragmentArgs by navArgs()
    private lateinit var favouritePhoto: FavouritePhoto

    private val requestWriteInMemoryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->
        if (permissionGranted) {
            viewLifecycleOwner.lifecycleScope.launch {
                photoDetailsViewModel.saveImageToInternalFolder(
                    favouritePhoto.imageHdUrl,
                    favouritePhoto.title
                )
                requireActivity().runOnUiThread { toast(getString(R.string.successfully_saved_picture)) }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("WrongConstant")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favouritePhoto = args.favouritePhoto

        //init photo and text load
        binding.apply {
            descriptionDetail.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
            descriptionDetail.text = favouritePhoto.explanation

            //todo переделать
            val filePath = ImageManager.getImageFullPathFile(favouritePhoto.title)
            Glide.with(root.context)
                .load(
                    if (filePath.exists())
                        BitmapFactory.decodeFile(filePath.absolutePath)
                    else
                        favouritePhoto.imageUrl
                )
                .placeholder(R.drawable.placeholder_400x400)
                .error(R.drawable.error_400x400)
                .listener(GlideRequestListener {
                    saveToGalleryBtn.isEnabled = false
                    setWallpapersBtn.isEnabled = false
                })
                .into(imageDetail)


            setAppBarTitle(favouritePhoto.title)

            dateDetail.text = favouritePhoto.date.toDefaultFormattedDate()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                photoDetailsViewModel.details.collect { state ->
                    updateUi(state)
                }
            }
        }

        binding.saveToGalleryBtn.setOnClickListener {
            requestWriteInMemoryPermission()
        }

        binding.setWallpapersBtn.setOnClickListener {
            setWallpaper(favouritePhoto.imageHdUrl, favouritePhoto.title)
        }
    }

    private fun requestWriteInMemoryPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showRationaleDialog(
                "Error",
                "Write external storage permission did not received! (message from dialog)"
            )
        } else {
            requestWriteInMemoryPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun showRationaleDialog(title: String, message: String) {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
            .setMessage(message)
            .setTitle(title)
            .setCancelable(false)
            .setPositiveButton(
                "OK"
            ) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    //todo add dialog with 3 choices: 1)homescreen 2)lockscreen 3)cancel
    private fun setWallpaper(url: String, title: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val wallpaperManager = WallpaperManager.getInstance(requireContext())
            val data = photoDetailsViewModel.getDataForWallpapers(url, title)
            wallpaperManager.setBitmap(data.first, data.second, false)
            photoDetailsViewModel.updateStateWallpapersSet()
            requireActivity().runOnUiThread { toast(getString(R.string.wallpapers_set)) }
        }
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