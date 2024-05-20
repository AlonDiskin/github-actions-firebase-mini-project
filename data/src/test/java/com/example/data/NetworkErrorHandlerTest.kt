package com.example.data

import com.example.data.remote.NetworkErrorHandler
import com.example.domain.model.NewsError
import com.google.common.truth.Truth.assertThat
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(JUnitParamsRunner::class)
class NetworkErrorHandlerTest {

    // Test subject
    private val errorHandler = NetworkErrorHandler()

    @Test
    @Parameters(method = "errorParams")
    fun mapNetworkErrorToAppError(networkError: Throwable,expectedAppError: NewsError) {
        // Given

        // When
        val actualAppError =  errorHandler.convertToAppError(networkError)

        // Then
        assertThat(actualAppError).isEqualTo(expectedAppError)
    }

    private fun errorParams() = arrayOf(
        arrayOf(
            IOException(),
            NewsError.DeviceNetwork,
        ),
        arrayOf(
            Exception(),
            NewsError.Internal,
        )
    )
}