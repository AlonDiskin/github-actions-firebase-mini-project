package com.example.ui.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ui.databinding.NewsItemBinding
import com.example.ui.model.NewsItemUiState

class NewsListAdapter(
    private val clickListener: (NewsItemUiState) -> (Unit)
) : ListAdapter<NewsItemUiState, NewsListAdapter.NewsItemViewHolder>(
    DIFF_CALLBACK
)  {

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsItemUiState>() {

            override fun areItemsTheSame(oldItem: NewsItemUiState, newItem: NewsItemUiState): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: NewsItemUiState, newItem: NewsItemUiState) =
                oldItem == newItem
        }
    }

    class NewsItemViewHolder(
        private val binding: NewsItemBinding,
        clickListener: (NewsItemUiState) -> (Unit)
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.itemClickListener = clickListener
        }

        fun bind(newsItem: NewsItemUiState) {
            binding.newsItem = newsItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsItemViewHolder {
        val binding = NewsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsItemViewHolder(binding,clickListener)
    }

    override fun onBindViewHolder(holder: NewsItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}