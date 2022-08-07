package ru.pl.astronomypictureoftheday.view.photodetails

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import java.io.File
import java.io.FileOutputStream
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
                delay(2000)
                val bitmap = downloadImage()
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

    private suspend fun downloadImage(): Bitmap? = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        try {
            val url = URL(topPhotoEntity.imageUrl)
            val inputStream = url.openStream()
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
            Toast.makeText(
                requireContext(),
                "$filePath successfully saved in Download Folder",
                Toast.LENGTH_SHORT
            ).show()
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