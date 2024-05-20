package com.example.github_actions_firebase_mini_project

import com.example.data.implementations.NewsRepositoryImpl
import com.example.domain.interfaces.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindNewsRepository(
        repo: NewsRepositoryImpl
    ): NewsRepository
}