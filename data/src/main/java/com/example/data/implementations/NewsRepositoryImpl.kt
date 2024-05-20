package com.example.data.implementations

import com.example.data.remote.RemoteNewsSource
import com.example.domain.interfaces.NewsRepository
import com.example.domain.model.NewsItem
import io.reactivex.Single
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(private val remoteSource: RemoteNewsSource) : NewsRepository {

    override fun getAll(): Single<Result<List<NewsItem>>> {
        return remoteSource.getNewsFeed()
    }
}