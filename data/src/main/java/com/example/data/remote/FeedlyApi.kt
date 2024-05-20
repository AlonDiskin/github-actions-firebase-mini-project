package com.example.data.remote

import com.example.data.model.FeedlyFeedResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedlyApi {

    @GET(FEEDLY_FEED_PATH)
    fun getTechNewsFeed(
        @Query(FEEDLY_FEED_ID_PARAM) streamId: String,
        @Query(FEEDLY_FEED_SIZE_PARAM) count: Int
    ): Single<FeedlyFeedResponse>
}