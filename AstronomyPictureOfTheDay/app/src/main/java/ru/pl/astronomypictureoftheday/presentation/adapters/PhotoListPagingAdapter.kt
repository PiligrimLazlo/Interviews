package ru.pl.astronomypictureoftheday.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.domain.PhotoEntity

private const val TAG = "PhotoListPagingAdapter"

class PhotoListPagingAdapter(
    private val onPhotoClickListener: (PhotoEntity) -> Unit,
    private val onSaveButtonPressedListener: (PhotoEntity) -> Unit,
) : PagingDataAdapter<PhotoEntity, PhotoViewHolder>(PhotoComparator()) {

    private var favList: List<PhotoEntity> = emptyList()

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
        var photoItem = getItem(position)

        for (favPhoto in favList) {
            if (photoItem?.title == favPhoto.title) {
                photoItem = favPhoto
            }
        }

        if (photoItem != null) {
            holder.bind(photoItem, onPhotoClickListener, onSaveButtonPressedListener)
        }
    }

    fun onChangeFavourites(favList: List<PhotoEntity>) {
        this.favList = favList
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_BIG_ITEM else VIEW_TYPE_REGULAR_ITEM
    }

    companion object {
        const val VIEW_TYPE_BIG_ITEM = 0
        const val VIEW_TYPE_REGULAR_ITEM = 1
    }
}
