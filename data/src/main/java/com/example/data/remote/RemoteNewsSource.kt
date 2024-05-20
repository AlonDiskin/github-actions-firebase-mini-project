package com.example.data.remote

import com.example.domain.model.NewsItem
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class RemoteNewsSource @Inject constructor(private val api: FeedlyApi,
                       private val dataMapper: NewsItemMapper,
                       private val errorHandler: NetworkErrorHandler) {

    fun getNewsFeed(): Single<Result<List<NewsItem>>> {
        return api.getTechNewsFeed(TECH_NEWS_FEED, FEEDLY_FEED_SIZE)
            .subscribeOn(Schedulers.io())
            .map { Result.success(dataMapper.map(it)) }
            .onErrorReturn { Result.failure(errorHandler.convertToAppError(it)) }
    }
}
