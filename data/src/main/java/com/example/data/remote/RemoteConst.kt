package com.example.data.remote

// Feedly api base url
const val FEEDLY_BASE = "https://cloud.feedly.com/v3/"

// Feedly api param names and values for requests
const val TECH_NEWS_FEED = "feed/http://theverge.com/rss/index.xml"
const val FEEDLY_FEED_PATH = "streams/contents"
const val FEEDLY_FEED_ID_PARAM = "streamId"
const val FEEDLY_FEED_SIZE_PARAM = "count"
const val FEEDLY_FEED_SIZE = 50