package ru.pl.astronomypictureoftheday.view.photodetails

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.api.TopPhotoEntity
import ru.pl.astronomypictureoftheday.databinding.FragmentPhotoDetailsBinding

class PhotoDetailsFragment: Fragment() {

    private var _binding: FragmentPhotoDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            getString(R.string.binding_null_error)
        }

    private val viewModel: PhotoDetailsViewModel by viewModels()
    private val args: PhotoDetailsFragmentArgs by navArgs()


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topPhotoEntity = args.photoEntity
        binding.apply {
            descriptionDetail.text = topPhotoEntity.explanation
            Glide.with(root.context)
                .load(topPhotoEntity.imageUrl)
                .placeholder(R.drawable.placeholder_400x400)
                .into(imageDetail)

            (activity as AppCompatActivity).supportActionBar?.title = topPhotoEntity.title
        }
    }

}