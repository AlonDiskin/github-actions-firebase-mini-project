package com.example.github_actions_firebase_mini_project.brows_news

import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.data.model.FeedlyFeedResponse
import com.example.data.remote.FEEDLY_FEED_PATH
import com.example.github_actions_firebase_mini_project.util.RecyclerViewMatcher.withRecyclerView
import com.example.github_actions_firebase_mini_project.util.isRecyclerViewItemsCount
import com.example.ui.R
import com.example.ui.controller.MainActivity
import com.example.ui.controller.NewsListAdapter.NewsItemViewHolder
import com.example.ui.model.NewsItemUiState
import com.example.ui.util.ImageLoader
import com.google.gson.Gson
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.mockkObject
import io.mockk.verify
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers
import org.robolectric.Shadows
import java.io.File

class LatestNewsShownSteps(private val server: MockWebServer) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<MainActivity>
    private val newsResPath = "json/news_update.json"
    private val dispatcher: Dispatcher

    init {
        mockkObject(ImageLoader)
        dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val feedPath = "/$FEEDLY_FEED_PATH"

                return when(request.requestUrl.uri().path) {
                    feedPath -> MockResponse()
                        .setBody(getJsonFromResource(newsResPath))
                        .setResponseCode(200)

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }
    }

    @Given("^news update is available$")
    fun newsUpdateIsAvailable() {
        server.setDispatcher(dispatcher)
    }

    @When("^he opens the news screen$")
    fun heOpensTheNewsScreen() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^latest news should be shown$")
    fun latestNewsShouldBeShown() {
        val expectedNewsItems = getExpectedUiNews()

        onView(withId(R.id.news_list))
            .check(matches(isRecyclerViewItemsCount(expectedNewsItems.size)))

        expectedNewsItems.forEachIndexed { index, item ->
            // Scroll to expected headline layout position
            onView(withId(R.id.news_list))
                .perform(
                    scrollToPosition<NewsItemViewHolder>(
                        index
                    )
                )

            // Check news data shown
            onView(withRecyclerView(R.id.news_list).atPosition(index))
                .check(
                    matches(
                        hasDescendant(
                            CoreMatchers.allOf(
                                withId(R.id.news_item_title),
                                withText(item.title),
                                isDisplayed()
                            )
                        )
                    )
                )

            verify { ImageLoader.loadIntoImageView(any(),item.imageUrl) }
        }
    }

    private fun getJsonFromResource(resource: String): String {
        val topLevelClass = object : Any() {}.javaClass.enclosingClass!!
        val jsonResource = topLevelClass.classLoader!!.getResource(resource)

        return File(jsonResource.toURI()).readText()
    }

    private fun getExpectedUiNews(): List<NewsItemUiState> {
        val gson = Gson()
        val serverJson = getJsonFromResource(newsResPath)
        val apiResponse = gson.fromJson(serverJson, FeedlyFeedResponse::class.java)

        return apiResponse.items.map {
            NewsItemUiState(it.id,it.title!!,"",it.visual!!.url,it.originId)
        }
    }
}