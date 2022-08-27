package ru.pl.astronomypictureoftheday.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.pl.astronomypictureoftheday.data.PhotoEntity

class PhotoComparator : DiffUtil.ItemCallback<PhotoEntity>() {
    override fun areItemsTheSame(oldItem: PhotoEntity, newItem: PhotoEntity): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: PhotoEntity, newItem: PhotoEntity): Boolean {
        return oldItem == newItem
    }
}