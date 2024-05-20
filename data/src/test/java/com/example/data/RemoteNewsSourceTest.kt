package com.example.data

import com.example.data.model.FeedlyFeedResponse
import com.example.data.remote.FEEDLY_FEED_SIZE
import com.example.data.remote.FeedlyApi
import com.example.data.remote.NetworkErrorHandler
import com.example.data.remote.NewsItemMapper
import com.example.data.remote.RemoteNewsSource
import com.example.data.remote.TECH_NEWS_FEED
import com.example.domain.model.NewsError
import com.example.domain.model.NewsItem
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class RemoteNewsSourceTest {


    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Test subject
    private lateinit var remoteNewsSource: RemoteNewsSource

    // Collaborators
    private val api: FeedlyApi = mockk()
    private val dataMapper: NewsItemMapper = mockk()
    private val errorHandler: NetworkErrorHandler = mockk()

    @Before
    fun setUp() {
        remoteNewsSource = RemoteNewsSource(api, dataMapper, errorHandler)
    }

    @Test
    fun loadNewsFeedFromRemoteApi_WhenFeedIsAsked_SuccessPath() {
        // Given
        val apiFeed = mockk<FeedlyFeedResponse>()
        val feed = emptyList<NewsItem>()

        every { api.getTechNewsFeed(TECH_NEWS_FEED, FEEDLY_FEED_SIZE) } returns Single.just(apiFeed)
        every { dataMapper.map(apiFeed) } returns feed

        // When
        val observer = remoteNewsSource.getNewsFeed().test()

        // Then
        observer.assertResult(Result.success(feed))
    }

    @Test
    fun loadNewsFeedFromRemoteApi_WhenFeedIsAsked_FailurePath() {
        // Given
        val error = Throwable()
        val appError = mockk<NewsError.RemoteServer>()

        every { api.getTechNewsFeed(any(),any()) } returns Single.error(error)
        every { errorHandler.convertToAppError(error) } returns appError

        // When
        val observer = remoteNewsSource.getNewsFeed().test()

        // Then
        observer.assertResult(Result.failure(appError))
    }
}