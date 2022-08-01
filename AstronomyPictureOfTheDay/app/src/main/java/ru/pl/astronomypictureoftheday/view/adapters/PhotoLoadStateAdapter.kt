package ru.pl.astronomypictureoftheday.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.pl.astronomypictureoftheday.R
import ru.pl.astronomypictureoftheday.databinding.PhotosLoadStateFooterBinding

class PhotoLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<PhotoLoadStateViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): PhotoLoadStateViewHolder {
        return PhotoLoadStateViewHolder.create(parent, retry)
    }

    override fun onBindViewHolder(holder: PhotoLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
}

class PhotoLoadStateViewHolder(
    private val binding: PhotosLoadStateFooterBinding,
    private val retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.retryFooterButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBarFooter.isVisible = loadState is LoadState.Loading
        binding.retryFooterButton.isVisible = loadState is LoadState.Error
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): PhotoLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.photos_load_state_footer, parent, false)
            val binding = PhotosLoadStateFooterBinding.bind(view)
            return PhotoLoadStateViewHolder(binding, retry)
        }
    }
}