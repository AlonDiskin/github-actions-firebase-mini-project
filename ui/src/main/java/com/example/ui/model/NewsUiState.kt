package com.example.ui.model

import com.example.domain.model.NewsError

data class NewsUiState(val items: List<NewsItemUiState> = emptyList(),
                       val isLoading: Boolean = false,
                       val error: NewsError? = null)