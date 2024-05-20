package com.example.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.domain.model.NewsError
import com.example.domain.usecase.GetNewsFeedUseCase
import com.example.ui.model.NewsItemUiState
import com.example.ui.model.NewsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val newsUseCase: GetNewsFeedUseCase,
                    private val newsMapper: NewsItemsUiMapper
) : ViewModel() {

    private val newsSubject = BehaviorSubject.create<Unit>()
    private val _uiState: MutableLiveData<NewsUiState> = MutableLiveData(NewsUiState())
    val uiState: LiveData<NewsUiState> get() = _uiState
    private val subscriptionContainer = SerialDisposable()

    init {
        // Create news subscription
        subscriptionContainer.set(createNewsSubscription())
        // Update news
        loadNews()
    }

    override fun onCleared() {
        super.onCleared()
        // Cancel news subscription
        subscriptionContainer.dispose()
    }

    fun reloadNews() {
        loadNews()
    }

    private fun createNewsSubscription(): Disposable {
        // Create rx chain for news subscription
        return newsSubject
            .switchMapSingle { newsUseCase.execute() }
            .map(newsMapper::map)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::handleNewsUpdate)
    }

    private fun loadNews() {
        _uiState.value = _uiState.value!!.copy(isLoading = true)
        newsSubject.onNext(Unit)
    }

    private fun handleNewsUpdate(result: Result<List<NewsItemUiState>>) {
        when {
            result.isSuccess -> handleUpdateSuccess(result.getOrNull()!!)
            result.isFailure -> handleUpdateFailure(result.exceptionOrNull()!!)
        }
    }

    private fun handleUpdateSuccess(newsItems: List<NewsItemUiState>) {
        _uiState.value = NewsUiState(newsItems)
    }

    private fun handleUpdateFailure(error: Throwable) {
        val newsError: NewsError = try {
            error as NewsError
        } catch (castingError: Throwable) {
            NewsError.Internal
        }

        _uiState.value = _uiState.value!!.copy(isLoading = false,error = newsError)
    }
}