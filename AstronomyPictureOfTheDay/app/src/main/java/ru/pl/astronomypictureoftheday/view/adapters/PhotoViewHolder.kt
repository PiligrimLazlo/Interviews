package ru.pl.astronomypictureoftheday.view.adapters

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.model.FavouritePhoto

//не использую viewBinding, т.к. будет 2 почти одинаковых класса с разными binding
class PhotoViewHolder(private val view: View) :
    RecyclerView.ViewHolder(view) {

    private val topPhotoTv = view.findViewById<TextView>(R.id.top_photo_tv)
    private val topPhotoImage = view.findViewById<ImageView>(R.id.top_photo_image)
    private val starBtn = view.findViewById<ImageButton>(R.id.star_btn)

    fun bind(
        favouritePhoto: FavouritePhoto,
        onPhotoClickListener: (FavouritePhoto) -> Unit,
        onSaveButtonPressedListener: (FavouritePhoto) -> Unit
    ) {
        topPhotoTv.text = favouritePhoto.title
        Glide.with(view.context)
            .load(favouritePhoto.imageUrl)
            .placeholder(R.drawable.placeholder_400x400)
            .error(R.drawable.error_400x400)
            .into(topPhotoImage)

        view.setOnClickListener {
            onPhotoClickListener(favouritePhoto)
        }

        starBtn.setOnClickListener {
            favouritePhoto.isFavourite = !favouritePhoto.isFavourite
            updateColor(favouritePhoto.isFavourite)
            onSaveButtonPressedListener(favouritePhoto)
        }
        updateColor(favouritePhoto.isFavourite)
    }

    private fun updateColor(isFavourite: Boolean) {
        starBtn.background.setTint(
            if (isFavourite) view.context.getColor(R.color.accent)
            else view.context.getColor(R.color.white)
        )
    }
}