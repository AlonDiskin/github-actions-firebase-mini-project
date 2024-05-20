package com.example.github_actions_firebase_mini_project.brows_news

import android.content.Context
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.ui.R
import com.example.ui.controller.MainActivity
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.allOf
import org.robolectric.Shadows

class NewsBrowsingFailSteps(private val server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<MainActivity>

    @Given("^app is unable to show news because off because off \"([^\"]*)\"$")
    fun appUnableToShowNews(errorType: String) {
        when(errorType) {
            "device connectivity" -> {
                // Shout down server to simulate that device is unconnected and cannot reach server
                server.shutdown()
            }
            "remote server" -> {
                val dispatcher = object : Dispatcher() {
                    override fun dispatch(request: RecordedRequest): MockResponse {
                        return MockResponse().setResponseCode(500)
                    }
                }

                server.setDispatcher(dispatcher)
            }
            else -> throw IllegalArgumentException("Unknown step arg:$errorType")
        }
    }

    @When("^user open news screen$")
    fun userOpenNewsScreen() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^news screen should show \"([^\"]*)\"$")
    fun newsScreenShouldShowMessage(expectedMessage: String) {
        val expectedSnackbarMessage = when(expectedMessage) {
            "device connection fail" -> ApplicationProvider.getApplicationContext<Context>()
                .getString(R.string.error_device_not_connected)
            "temporarily unavailable" -> ApplicationProvider.getApplicationContext<Context>()
                .getString(R.string.error_feature_unavailable)
            else -> throw IllegalArgumentException("Unknown step arg:$expectedMessage")
        }

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(
                matches(
                    allOf(
                        withText(expectedSnackbarMessage),
                        isDisplayed()
                    )
                )
            )
        onView(withId(com.google.android.material.R.id.snackbar_action))
            .check(
                matches(
                    allOf(
                        withText(R.string.action_retry),
                        isDisplayed()
                    )
                )
            )
    }
}