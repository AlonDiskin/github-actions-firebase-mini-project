package com.example.domain.usecase

import com.example.domain.interfaces.NewsRepository
import com.example.domain.model.NewsItem
import io.reactivex.Single
import javax.inject.Inject

class GetNewsFeedUseCase @Inject constructor(private val repository: NewsRepository) {

    fun execute(): Single<Result<List<NewsItem>>> {
        return repository.getAll()
    }
}