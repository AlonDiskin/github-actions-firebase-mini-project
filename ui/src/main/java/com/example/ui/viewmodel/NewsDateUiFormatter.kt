package com.example.ui.viewmodel

import org.joda.time.LocalDateTime
import javax.inject.Inject

const val DATE_FORMAT = "dd MMM HH:mm"

class NewsDateUiFormatter @Inject constructor() {

    fun format(published: Long): String {
        return LocalDateTime(published).toString(DATE_FORMAT)
    }
}