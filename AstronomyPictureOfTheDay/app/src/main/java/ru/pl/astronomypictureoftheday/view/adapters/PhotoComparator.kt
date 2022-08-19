package ru.pl.astronomypictureoftheday.view.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.pl.astronomypictureoftheday.model.FavouritePhoto

class PhotoComparator : DiffUtil.ItemCallback<FavouritePhoto>() {
    override fun areItemsTheSame(oldItem: FavouritePhoto, newItem: FavouritePhoto): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: FavouritePhoto, newItem: FavouritePhoto): Boolean {
        return oldItem == newItem
    }
}