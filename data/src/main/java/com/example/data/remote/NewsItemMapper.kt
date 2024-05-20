package com.example.data.remote

import com.example.data.model.FeedlyFeedResponse
import com.example.domain.model.NewsItem
import javax.inject.Inject

class NewsItemMapper @Inject constructor(){

    fun map(feed: FeedlyFeedResponse): List<NewsItem> {
        return feed.items.map {
            NewsItem(it.id,
                it.title ?: "",
                it.originId,
                it.visual?.url ?: "",
                it.published)
        }
    }
}