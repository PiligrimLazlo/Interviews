package ru.pl.astronomypictureoftheday.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.BigPhotoListItemBinding
import ru.pl.astronomypictureoftheday.databinding.RegularPhotoListItemBinding
import ru.pl.astronomypictureoftheday.model.FavouritePhoto
import java.lang.IllegalArgumentException

class PhotoListPagingAdapter(
    private val onPhotoClickListener: (FavouritePhoto) -> Unit,
    private val onSaveButtonPressedListener: (FavouritePhoto) -> Unit,
) : PagingDataAdapter<FavouritePhoto, RecyclerView.ViewHolder>(TopPhotoComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_BIG_ITEM) {
            val binding = BigPhotoListItemBinding.inflate(inflater, parent, false)
            BigPhotoViewHolder(binding)
        } else if (viewType == VIEW_TYPE_REGULAR_ITEM) {
            val binding = RegularPhotoListItemBinding.inflate(inflater, parent, false)
            RegularPhotoViewHolder(binding)
        } else {
            throw IllegalArgumentException("View type must be 0 or 1")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val photoItem = getItem(position)
        if (photoItem != null) {
            if (holder is RegularPhotoViewHolder)
                holder.bind(photoItem, onPhotoClickListener, onSaveButtonPressedListener)
            else if (holder is BigPhotoViewHolder)
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


//todo вынести повторный код в общего родителя (но как)
class RegularPhotoViewHolder(private val binding: RegularPhotoListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        favouritePhoto: FavouritePhoto,
        onPhotoClickListener: (FavouritePhoto) -> Unit,
        onSaveButtonPressedListener: (FavouritePhoto) -> Unit
    ) {
        binding.topPhotoTv.text = favouritePhoto.title
        Glide.with(binding.root.context)
            .load(favouritePhoto.imageUrl)
            .placeholder(R.drawable.placeholder_200x200)
            .error(R.drawable.error_200x200)
            .into(binding.topPhotoImage)

        binding.root.setOnClickListener {
            onPhotoClickListener(favouritePhoto)
        }

        binding.starBtn.setOnClickListener {
            favouritePhoto.isFavourite = !favouritePhoto.isFavourite
            updateColor(favouritePhoto.isFavourite)

            onSaveButtonPressedListener(favouritePhoto)
        }

        updateColor(favouritePhoto.isFavourite)
    }

    private fun updateColor(isFavourite: Boolean) {
        binding.starBtn.background.setTint(
            if (isFavourite) binding.root.context.getColor(R.color.accent)
            else binding.root.context.getColor(R.color.white)
        )
    }
}

class BigPhotoViewHolder(private val binding: BigPhotoListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        favouritePhoto: FavouritePhoto,
        onPhotoClickListener: (FavouritePhoto) -> Unit,
        onSaveButtonPressedListener: (FavouritePhoto) -> Unit
    ) {
        binding.topPhotoTv.text = favouritePhoto.title
        Glide.with(binding.root.context)
            .load(favouritePhoto.imageUrl)
            .placeholder(R.drawable.placeholder_400x400)
            .error(R.drawable.error_400x400)
            .into(binding.topPhotoImage)

        binding.root.setOnClickListener {
            onPhotoClickListener(favouritePhoto)
        }

        binding.starBtn.setOnClickListener {
            favouritePhoto.isFavourite = !favouritePhoto.isFavourite

            onSaveButtonPressedListener(favouritePhoto)
            updateColor(favouritePhoto.isFavourite)
        }

        updateColor(favouritePhoto.isFavourite)
    }

    private fun updateColor(isFavourite: Boolean) {
        binding.starBtn.background.setTint(
            if (isFavourite) binding.root.context.getColor(R.color.accent)
            else binding.root.context.getColor(R.color.white)
        )
    }
}
