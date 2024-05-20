package com.example.domain.model

sealed class NewsError : Throwable() {

    object Internal : NewsError()

    object RemoteServer : NewsError()

    object DeviceNetwork : NewsError()
}