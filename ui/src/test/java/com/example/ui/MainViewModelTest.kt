package com.example.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.domain.model.NewsError
import com.example.domain.model.NewsItem
import com.example.domain.usecase.GetNewsFeedUseCase
import com.example.ui.model.NewsItemUiState
import com.example.ui.model.NewsUiState
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NewsItemsUiMapper
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    companion object {

        @JvmStatic
        @BeforeClass
        fun setupClass() {
            // Set Rx framework for testing
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }
    }

    // Lifecycle testing rule
    @JvmField
    @Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test subject
    private lateinit var viewModel: MainViewModel

    // Collaborators
    private val getNewsUseCase: GetNewsFeedUseCase = mockk()
    private val newsItemsMapper: NewsItemsUiMapper = mockk()

    // Stub data
    private val newsUpdateSubject = SingleSubject.create<Result<List<NewsItem>>>()

    @Before
    fun setUp() {
        // Stub collaborators
        every { getNewsUseCase.execute() } returns newsUpdateSubject

        viewModel = MainViewModel(getNewsUseCase,newsItemsMapper)
    }

    @Test
    fun setNewsStateAsLoading_WhenCreated() {
        // Given
        val expected = NewsUiState(isLoading = true)

        // Then
        val actual = viewModel.uiState.value!!
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun loadNews_WhenCreated() {
        // Given

        // Then
        verify(exactly = 1) { getNewsUseCase.execute() }
    }

    @Test
    fun updateNewsState_WhenNewsLoadedSuccessfully() {
        // Given
        val newsUpdate: Result<List<NewsItem>> = mockk()
        val mappedNewsUpdate: List<NewsItemUiState> = mockk()
        val expectedNewsState = NewsUiState(mappedNewsUpdate)

        every { newsItemsMapper.map(newsUpdate) } returns Result.success(mappedNewsUpdate)

        // When
        newsUpdateSubject.onSuccess(newsUpdate)

        // Then
        assertThat(viewModel.uiState.value).isEqualTo(expectedNewsState)
    }

    @Test
    fun updateNewsState_WhenNewsLoadFail() {
        // Given
        val loadError = NewsError.Internal
        val newsUpdate: Result<List<NewsItem>> = mockk()
        val mappedNewsUpdate = Result.failure<List<NewsItemUiState>>(loadError)
        val expectedNewsState = NewsUiState(error = loadError)

        every { newsItemsMapper.map(newsUpdate) } returns  mappedNewsUpdate

        // When
        newsUpdateSubject.onSuccess(newsUpdate)

        // Then
        assertThat(viewModel.uiState.value).isEqualTo(expectedNewsState)
    }

    @Test
    fun enableNewsReload_WhenRequested() {
        // Given
        val expectedNewsSate = NewsUiState(isLoading = true)

        // When
        viewModel.reloadNews()

        // Then
        verify(exactly = 2) { getNewsUseCase.execute() }
        assertThat(viewModel.uiState.value).isEqualTo(expectedNewsSate)
    }
}