package com.example.domain

import com.example.domain.interfaces.NewsRepository
import com.example.domain.model.NewsItem
import com.example.domain.usecase.GetNewsFeedUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test

class GetNewsFeedUseCaseTest {

    // Test subject
    private lateinit var useCase: GetNewsFeedUseCase

    // Collaborators
    private val repository: NewsRepository = mockk()

    @Before
    fun setUp() {
        useCase = GetNewsFeedUseCase(repository)
    }

    @Test
    fun deliverFeed_WhenExecuted() {
        // Given
        val feed = mockk<Single<Result<List<NewsItem>>>>()

        every { repository.getAll() } returns feed

        // When
        val useCaseResult = useCase.execute()

        // Then
        verify { repository.getAll() }
        assertThat(useCaseResult).isEqualTo(feed)
    }
}