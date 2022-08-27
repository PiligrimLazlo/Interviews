package ru.pl.astronomypictureoftheday.presentation.adapters

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.data.PhotoEntity

//не использую viewBinding, т.к. будет 2 почти одинаковых класса с разными binding
class PhotoViewHolder(private val view: View) :
    RecyclerView.ViewHolder(view) {

    private val topPhotoTv = view.findViewById<TextView>(R.id.top_photo_tv)
    private val topPhotoImage = view.findViewById<ImageView>(R.id.top_photo_image)
    private val starBtn = view.findViewById<ImageButton>(R.id.star_btn)

    fun bind(
        photoEntity: PhotoEntity,
        onPhotoClickListener: (PhotoEntity) -> Unit,
        onSaveButtonPressedListener: (PhotoEntity) -> Unit
    ) {
        topPhotoTv.text = photoEntity.title
        Glide.with(view.context)
            .load(photoEntity.imageUrl)
            .placeholder(R.drawable.placeholder_400x400)
            .error(R.drawable.error_400x400)
            .into(topPhotoImage)

        view.setOnClickListener {
            onPhotoClickListener(photoEntity)
        }

        starBtn.setOnClickListener {
            photoEntity.isFavourite = !photoEntity.isFavourite
            updateColor(photoEntity.isFavourite)
            onSaveButtonPressedListener(photoEntity)
        }
        updateColor(photoEntity.isFavourite)
    }

    private fun updateColor(isFavourite: Boolean) {
        starBtn.background.setTint(
            if (isFavourite) view.context.getColor(R.color.accent)
            else view.context.getColor(R.color.white)
        )
    }
}