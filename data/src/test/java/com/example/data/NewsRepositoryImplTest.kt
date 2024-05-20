package com.example.data

import com.example.data.implementations.NewsRepositoryImpl
import com.example.data.remote.RemoteNewsSource
import com.example.domain.model.NewsItem
import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class NewsRepositoryImplTest {

    // Test subject
    private lateinit var repository: NewsRepositoryImpl

    // Collaborators
    private val remoteNewsSource: RemoteNewsSource = mockk()

    @Before
    fun setUp() {
        repository = NewsRepositoryImpl(remoteNewsSource)
    }

    @Test
    fun loadRemoteNewsFeed_WhenQueriedForAllItems() {
        // Given
        val feed = mockk<Single<Result<List<NewsItem>>>>()

        every { remoteNewsSource.getNewsFeed() } returns feed

        // When
        val repositoryResult = repository.getAll()

        // Then
        verify { remoteNewsSource.getNewsFeed() }
        Truth.assertThat(repositoryResult).isEqualTo(feed)
    }
}