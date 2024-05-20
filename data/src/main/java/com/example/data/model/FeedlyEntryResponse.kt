package com.example.data.model

data class FeedlyEntryResponse(val id: String,
                               val title: String?,
                               val visual: Visual?,
                               val originId: String,
                               val published: Long) {

    data class Visual(val url: String)
}