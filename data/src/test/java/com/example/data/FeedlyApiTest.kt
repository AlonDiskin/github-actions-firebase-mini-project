package com.example.data

import com.example.data.model.FeedlyFeedResponse
import com.example.data.remote.FEEDLY_FEED_ID_PARAM
import com.example.data.remote.FEEDLY_FEED_PATH
import com.example.data.remote.FEEDLY_FEED_SIZE
import com.example.data.remote.FEEDLY_FEED_SIZE_PARAM
import com.example.data.remote.FeedlyApi
import com.example.data.remote.TECH_NEWS_FEED
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

/**
 * [FeedlyApi] integration test.
 */
class FeedlyApiTest {

    private lateinit var api: FeedlyApi
    private val server = MockWebServer()

    @Before
    fun setUp() {
        // Start mocked web server
        server.start()

        // Init feedly api client
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
            .create(FeedlyApi::class.java)
    }

    @Test
    fun getFeedFromServerAndMapResponse() {
        // Given remote api server is running and api client is initialized
        val dispatcher = object : Dispatcher() {
            val feedResourcePath = "feedly_news_response.json"
            val feedId = TECH_NEWS_FEED

            override fun dispatch(request: RecordedRequest): MockResponse {
                val feedPath = "/$FEEDLY_FEED_PATH"
                val feedSize = FEEDLY_FEED_SIZE

                return when(request.requestUrl.uri().path) {
                    feedPath -> {
                        return if (
                            request.requestUrl.queryParameter(FEEDLY_FEED_ID_PARAM) == feedId &&
                            request.requestUrl.queryParameter(FEEDLY_FEED_SIZE_PARAM) == feedSize.toString())
                        {
                            MockResponse()
                                .setBody(getJsonFromResource(feedResourcePath))
                                .setResponseCode(200)
                        } else {
                            MockResponse().setResponseCode(404)
                        }
                    }

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
        server.setDispatcher(dispatcher)

        // When
        val testObserver = api.getTechNewsFeed(TECH_NEWS_FEED, FEEDLY_FEED_SIZE).test()

        // Then
        val gson = Gson()
        val serverJson = getJsonFromResource(dispatcher.feedResourcePath)
        val expectedApiResponse = gson.fromJson(serverJson, FeedlyFeedResponse::class.java)
        testObserver.assertValue { it == expectedApiResponse }
    }

    private fun getJsonFromResource(resource: String): String {
        val topLevelClass = object : Any() {}.javaClass.enclosingClass!!
        val jsonResource = topLevelClass.classLoader!!.getResource(resource)

        return File(jsonResource.toURI()).readText()
    }
}