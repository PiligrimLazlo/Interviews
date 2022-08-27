package ru.pl.astronomypictureoftheday.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.data.PhotoEntity

class PhotoListFavouritesAdapter(
    private val onPhotoClickListener: (PhotoEntity) -> Unit,
    private val onSaveButtonPressedListener: (PhotoEntity) -> Unit,
) : ListAdapter<PhotoEntity, PhotoViewHolder>(PhotoComparator()) {

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