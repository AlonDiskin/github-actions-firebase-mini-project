package com.example.ui.viewmodel

import com.example.domain.model.NewsItem
import com.example.ui.model.NewsItemUiState
import javax.inject.Inject

class NewsItemsUiMapper @Inject constructor(
    private val dateFormatter: NewsDateUiFormatter
) {

    fun map(result: Result<List<NewsItem>>): Result<List<NewsItemUiState>> {
        return when(result.isSuccess) {
            true -> Result.success(
                result.getOrNull()!!.map { item ->
                    NewsItemUiState(
                        item.id,
                        item.title,
                        dateFormatter.format(item.published),
                        item.imageUrl,
                        item.originUrl
                    )
                }
            )
            else -> Result.failure(result.exceptionOrNull()!!)
        }
    }
}