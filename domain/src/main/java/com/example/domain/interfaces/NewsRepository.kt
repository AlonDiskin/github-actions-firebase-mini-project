package com.example.domain.interfaces

import com.example.domain.model.NewsItem
import io.reactivex.Single

interface NewsRepository {

    fun getAll(): Single<Result<List<NewsItem>>>
}