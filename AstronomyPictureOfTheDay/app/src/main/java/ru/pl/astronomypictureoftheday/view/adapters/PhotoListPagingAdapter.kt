package ru.pl.astronomypictureoftheday.view.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.model.FavouritePhoto

private const val TAG = "PhotoListPagingAdapter"

class PhotoListPagingAdapter(
    private val onPhotoClickListener: (FavouritePhoto) -> Unit,
    private val onSaveButtonPressedListener: (FavouritePhoto) -> Unit,
) : PagingDataAdapter<FavouritePhoto, PhotoViewHolder>(PhotoComparator()) {

    //НЕ через binding (для разнообразия)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_REGULAR_ITEM -> R.layout.regular_photo_list_item
            VIEW_TYPE_BIG_ITEM -> R.layout.big_photo_list_item
            else -> throw IllegalArgumentException(parent.resources.getString(R.string.view_type_exception))
        }
        val inflater = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return PhotoViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoItem = getItem(position)
        if (photoItem != null) {
            holder.bind(photoItem, onPhotoClickListener, onSaveButtonPressedListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_BIG_ITEM else VIEW_TYPE_REGULAR_ITEM
    }

    companion object {
        const val VIEW_TYPE_BIG_ITEM = 0
        const val VIEW_TYPE_REGULAR_ITEM = 1
    }
}
