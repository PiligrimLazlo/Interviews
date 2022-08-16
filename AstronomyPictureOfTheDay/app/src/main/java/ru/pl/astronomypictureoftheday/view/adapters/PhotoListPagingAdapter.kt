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
) : PagingDataAdapter<FavouritePhoto, PhotoViewHolder>(TopPhotoComparator) {

    private var count = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_REGULAR_ITEM -> R.layout.regular_photo_list_item
            VIEW_TYPE_BIG_ITEM -> R.layout.big_photo_list_item
            else -> throw IllegalArgumentException("View type must be 0 or 1")
        }
        ++count
        Log.d(TAG, "onCreateViewHolder() count: $count")
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

object TopPhotoComparator : DiffUtil.ItemCallback<FavouritePhoto>() {
    override fun areItemsTheSame(oldItem: FavouritePhoto, newItem: FavouritePhoto): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: FavouritePhoto, newItem: FavouritePhoto): Boolean {
        return oldItem == newItem
    }
}


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
