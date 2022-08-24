package ru.pl.astronomypictureoftheday.view.adapters

import androidx.recyclerview.widget.DiffUtil
import ru.pl.astronomypictureoftheday.model.PhotoEntity

class PhotoComparator : DiffUtil.ItemCallback<PhotoEntity>() {
    override fun areItemsTheSame(oldItem: PhotoEntity, newItem: PhotoEntity): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: PhotoEntity, newItem: PhotoEntity): Boolean {
        return oldItem == newItem
    }
}