package ru.pl.astronomypictureoftheday.view.photodetails

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.FragmentPhotoDetailsBinding
import ru.pl.astronomypictureoftheday.utils.setAppBarTitle
import ru.pl.astronomypictureoftheday.utils.toDefaultFormattedDate

class PhotoDetailsFragment : Fragment() {

    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            getString(R.string.binding_null_error)
        }

    private val viewModel: PhotoDetailsViewModel by viewModels()
    private val args: PhotoDetailsFragmentArgs by navArgs()

    private val requestWriteInMemoryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionGranted ->
        if (permissionGranted) {
            //todo: save photo
            Toast.makeText(
                context,
                "Write external storage permission granted! (message from launcher)",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            //можно ничего не делать
            Toast.makeText(
                context,
                "Write external storage permission did not received! (message from launcher)",
                Toast.LENGTH_SHORT
            ).show()
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

        val topPhotoEntity = args.photoEntity

        //photo load
        binding.apply {
            descriptionDetail.justificationMode = Layout.JUSTIFICATION_MODE_INTER_WORD
            descriptionDetail.text = topPhotoEntity.explanation
            Glide.with(root.context)
                .load(topPhotoEntity.imageUrl)
                .placeholder(R.drawable.placeholder_400x400)
                .error(R.drawable.error_400x400)
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
            showRationalDialog(
                "Error",
                "Write external storage permission did not received! (message from dialog)"
            )
        } else {
            requestWriteInMemoryPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun showRationalDialog(title: String, message: String) {
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

}