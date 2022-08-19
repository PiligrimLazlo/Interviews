package ru.pl.astronomypictureoftheday.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.FragmentFavouritesBinding
import ru.pl.astronomypictureoftheday.model.FavouritePhoto

class PhotoListFavouritesAdapter(
    private val onPhotoClickListener: (FavouritePhoto) -> Unit,
    private val onSaveButtonPressedListener: (FavouritePhoto) -> Unit,
) : ListAdapter<FavouritePhoto, PhotoViewHolder>(PhotoComparator()) {

    //через binding не получится, т.к. в ViewHolder через findViewById
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PhotoViewHolder(inflater.inflate(R.layout.regular_photo_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoItem = getItem(position)
        if (photoItem != null) {
            holder.bind(photoItem, onPhotoClickListener, onSaveButtonPressedListener)
        }
    }
}