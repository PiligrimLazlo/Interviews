package ru.pl.astronomypictureoftheday.view.photodetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.FragmentPhotoDetailsBinding
import ru.pl.astronomypictureoftheday.model.TopPhotoEntity
import ru.pl.astronomypictureoftheday.utils.setAppBarTitle
import ru.pl.astronomypictureoftheday.utils.showAlertDialog
import ru.pl.astronomypictureoftheday.utils.toDefaultFormattedDate
import ru.pl.astronomypictureoftheday.utils.toast
import java.io.File
import java.io.FileOutputStream
import java.lang.IllegalArgumentException
import java.net.URL

class PhotoDetailsFragment : Fragment() {

    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            getString(R.string.binding_null_error)
        }

    private val viewModel: PhotoDetailsViewModel by viewModels()
    private val args: PhotoDetailsFragmentArgs by navArgs()
    private lateinit var topPhotoEntity: TopPhotoEntity

    private val requestWriteInMemoryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->
        if (permissionGranted) {
            changeDownloadState(true)
            lifecycleScope.launch {
                //todo remove later
                delay(2000)
                val bitmap = getBitmapFromUrl(topPhotoEntity.imageUrl)
                bitmap?.let {
                    saveImageToInternalFolder(bitmap)
                }
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

        topPhotoEntity = args.photoEntity

        //photo load
        binding.apply {
            descriptionDetail.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
            descriptionDetail.text = topPhotoEntity.explanation
            Glide.with(root.context)
                .load(topPhotoEntity.imageUrl)
                .placeholder(R.drawable.placeholder_400x400)
                .error(R.drawable.error_400x400)
                .listener(GlideRequestListener {
                    saveToGalleryBtn.isEnabled = false
                    setWallpapersBtn.isEnabled = false
                })
                .into(imageDetail)

            setAppBarTitle(topPhotoEntity.title)

            dateDetail.text = topPhotoEntity.date.toDefaultFormattedDate()
        }

        binding.saveToGalleryBtn.setOnClickListener {
            requestWriteInMemoryPermission()
        }

        binding.setWallpapersBtn.setOnClickListener {
            lifecycleScope.launch { setWallpaper(topPhotoEntity.imageUrl) }
        }

    }

    private fun requestWriteInMemoryPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            showAlertDialog(
                "Error",
                "Write external storage permission did not received! (message from dialog)"
            )
        } else {
            requestWriteInMemoryPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private suspend fun getBitmapFromUrl(url: String): Bitmap? = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            val urlEntity = URL(url)
            val inputStream = urlEntity.openStream()
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext bitmap
    }

    private suspend fun saveImageToInternalFolder(bitmap: Bitmap) = withContext(Dispatchers.IO) {

        val fileName = "NasaAPOD_" + System.currentTimeMillis() / 1000 + ".png"

        val filePath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            fileName
        )
        FileOutputStream(filePath).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        requireActivity().runOnUiThread {
            toast("$filePath ${getString(R.string.successfully_saved_picture)}")
            changeDownloadState(false)
        }
    }

    //todo вынести в состояние viewModel, при повороте логика ломается и не по ООПшному
    private fun changeDownloadState(isDownloading: Boolean) {
        if (isDownloading) {
            binding.apply {
                saveToGalleryBtn.visibility = View.INVISIBLE
                progressBarSave.visibility = View.VISIBLE
            }
        } else {
            binding.apply {
                saveToGalleryBtn.visibility = View.VISIBLE
                progressBarSave.visibility = View.INVISIBLE
            }
        }
    }

    private suspend fun setWallpaper(url: String) = withContext(Dispatchers.IO) {
        val wallpaperManager = WallpaperManager.getInstance(requireContext())
        var bitmap = getBitmapFromUrl(url) ?: throw IllegalArgumentException()

        val wallpaperHeight = Resources.getSystem().displayMetrics.heightPixels
        val wallpaperWidth = Resources.getSystem().displayMetrics.widthPixels

        val widthFactor = wallpaperWidth.toDouble() / bitmap.width
        val heightFactor = wallpaperHeight.toDouble() / bitmap.height
        //scale (grow) bitmap if it smaller than screen
        //TODO chech it out
        /*if (bitmap.width < wallpaperWidth && widthFactor > heightFactor) {
            val newBitmapHeight = (bitmap.height * widthFactor).toInt()
            bitmap = bitmap.scale(wallpaperWidth, newBitmapHeight, false)
        }
        if (bitmap.height < wallpaperHeight && heightFactor > widthFactor) {
            val newBitmapWidth = (bitmap.width * heightFactor).toInt()
            bitmap = bitmap.scale(newBitmapWidth, wallpaperHeight, false)
        }*/

        //center cropping big image
        val start = Point(0, 0)
        val end = Point(bitmap.width, bitmap.height)

        if (bitmap.width > wallpaperWidth) {
            start.x = (bitmap.width - wallpaperWidth) / 2
            end.x = start.x + wallpaperWidth
        }
        if (bitmap.height > wallpaperHeight) {
            start.y = (bitmap.height - wallpaperHeight) / 2
            end.y = start.y + wallpaperHeight
        }

        wallpaperManager.setBitmap(bitmap, Rect(start.x, start.y, end.x, end.y), false)

        requireActivity().runOnUiThread { toast(getString(R.string.done)) }
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