package com.example.data

import com.example.data.model.FeedlyEntryResponse
import com.example.data.model.FeedlyEntryResponse.Visual
import com.example.data.model.FeedlyFeedResponse
import com.example.data.remote.NewsItemMapper
import com.example.domain.model.NewsItem
import com.google.common.truth.Truth.assertThat
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class NewsItemMapperTest {

    // Test subject
    private val mapper = NewsItemMapper()

    @Test
    @Parameters(method = "feedMappingParams")
    fun mapFeedlyNewsFeedToAppDataModel(feedlyFeedResponse: FeedlyFeedResponse,expectedAppFeed: List<NewsItem>) {
        // Given

        // When
        val actualAppFeed = mapper.map(feedlyFeedResponse)

        // Then
        assertThat(actualAppFeed).isEqualTo(expectedAppFeed)
    }

    private fun feedMappingParams() = arrayOf(
        arrayOf(
            FeedlyFeedResponse(
                listOf(
                    FeedlyEntryResponse(
                        "id_1","title_1",
                        Visual("imageUrl_1"),
                        "origin_1",
                        1L
                    )
                )
            ),
            listOf(
                NewsItem(
                    "id_1",
                    "title_1",
                    "origin_1",
                    "imageUrl_1",
                    1L
                )
            ),
        ),
        arrayOf(
            FeedlyFeedResponse(
                listOf(
                    FeedlyEntryResponse(
                        "id_1",
                        null,
                        null,
                        "origin_1",
                        2L
                    )
                )
            ),
            listOf(
                NewsItem(
                    "id_1",
                    "",
                    "origin_1",
                    "",
                    2L
                )
            ),
        )
    )
}