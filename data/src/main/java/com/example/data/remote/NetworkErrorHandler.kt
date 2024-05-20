package com.example.data.remote

import com.example.domain.model.NewsError
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class NetworkErrorHandler @Inject constructor(){

    fun convertToAppError(networkError: Throwable): NewsError {
        return when (networkError) {
            is IOException -> NewsError.DeviceNetwork
            is HttpException -> NewsError.RemoteServer
            else -> NewsError.Internal
        }
    }
}