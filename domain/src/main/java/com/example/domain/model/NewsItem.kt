package com.example.domain.model

data class NewsItem(val id: String,
                    val title: String,
                    val originUrl: String,
                    val imageUrl: String,
                    val published: Long)