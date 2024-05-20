package com.example.ui

import android.content.Context
import android.content.Intent
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelLazy
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domain.model.NewsError
import com.example.ui.RecyclerViewMatcher.withRecyclerView
import com.example.ui.controller.MainActivity
import com.example.ui.controller.NewsListAdapter.*
import com.example.ui.model.NewsItemUiState
import com.example.ui.model.NewsUiState
import com.example.ui.util.ImageLoader
import com.example.ui.viewmodel.MainViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.verify
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode
import android.net.Uri

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class MainActivityTest {

    // System under test
    private lateinit var scenario: ActivityScenario<MainActivity>

    // Collaborators
    private val viewModel = mockk<MainViewModel>()

    // Stub data
    private val uiState = MutableLiveData<NewsUiState>()

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<MainViewModel>>().value } returns viewModel

        // Stub mocked view model
        every { viewModel.uiState } returns uiState

        // Launch activity under test
        scenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun showLatestNews_WhenResumed() {
        // Given
        val newsItems = listOf(
            NewsItemUiState("id_1","title_1","published_1","url_1","origin_1"),
            NewsItemUiState("id_2","title_2","published_2","url_2","origin_2"),
            NewsItemUiState("id_3","title_3","published_3","url_3","origin_1")
        )

        mockkObject(ImageLoader)

        // When
        uiState.value = NewsUiState(newsItems,false)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withId(R.id.news_list))
            .check(matches(isRecyclerViewItemsCount(newsItems.size)))

        newsItems.forEachIndexed { index, item ->
            // Scroll to expected headline layout position
            onView(withId(R.id.news_list))
                .perform(scrollToPosition<NewsItemViewHolder>(index))

            // Check news data shown
            onView(withRecyclerView(R.id.news_list).atPosition(index))
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.news_item_published),
                                withText(item.published)
                            )
                        )
                    )
                )
                .check(
                    matches(
                        hasDescendant(
                            allOf(
                                withId(R.id.news_item_title),
                                withText(item.title),
                                isDisplayed()
                            )
                        )
                    )
                )

            // Check news image was loaded
            verify { ImageLoader.loadIntoImageView(any(),item.imageUrl) }
        }
    }

    @Test
    fun showProgressBar_WhenNewsLoaded() {
        // Given

        // Then
        onView(withId(R.id.progress_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun hideProgressBar_WhenNewsFetched() {
        // Given
        val stateUpdate = NewsUiState(emptyList(),false)

        // When
        uiState.value = stateUpdate
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun hideProgressBar_WhenFailedToLoadNews() {
        // Given
        val stateUpdate = NewsUiState(emptyList(),false,NewsError.Internal)

        // When
        uiState.value = stateUpdate
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun showFeatureUnavailableMessage_WhenFailedToLoadNewsBecauseOfUnknownError() {
        // Given
        val stateUpdate = NewsUiState(emptyList(),false,NewsError.Internal)

        // When
        uiState.value = stateUpdate
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        val errorMessage = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.error_feature_unavailable)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(
                allOf(
                    withText(errorMessage),
                    isDisplayed()
                )
            ))
        onView(withId(com.google.android.material.R.id.snackbar_action))
            .check(matches(
                allOf(
                    withText(R.string.action_retry),
                    isDisplayed()
                )
            ))
    }

    @Test
    fun showFeatureUnavailableMessage_WhenFailedToLoadNewsBecauseOfRemoteServerError() {
        // Given
        val stateUpdate = NewsUiState(emptyList(),false,NewsError.RemoteServer)

        // When
        uiState.value = stateUpdate
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        val errorMessage = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.error_feature_unavailable)
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(
                allOf(
                    withText(errorMessage),
                    isDisplayed()
                )
            ))
        onView(withId(com.google.android.material.R.id.snackbar_action))
            .check(matches(
                allOf(
                    withText(R.string.action_retry),
                    isDisplayed()
                )
            ))
    }

    @Test
    fun showDeviceConnectionMessageWithRetryOption_WhenFailedToLoadNewsBecauseOfDeviceConnectionError() {
        // Given
        val stateUpdate = NewsUiState(emptyList(),false,NewsError.DeviceNetwork)

        // When
        uiState.value = stateUpdate
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        val errorMessage = ApplicationProvider.getApplicationContext<Context>()
            .getString(R.string.error_device_not_connected)

        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(
                allOf(
                    withText(errorMessage),
                    isDisplayed()
                )
            ))
        onView(withId(com.google.android.material.R.id.snackbar_action))
            .check(matches(
                allOf(
                    withText(R.string.action_retry),
                    isDisplayed()
                )
            ))
    }

    @Test
    fun showDeviceAppChooser_WhenArticleSelected() {
        // Given
        val newsItems = listOf(
            NewsItemUiState("id_1","title_1","published_1","url_1","origin_1"),
        )

        Intents.init()

        // When
        uiState.value = NewsUiState(newsItems,false)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        onView(withText(newsItems.first().title))
            .perform(click())

        // Then
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent

        assertThat(intent.action).isEqualTo(Intent.ACTION_VIEW)
        assertThat(intent.data).isEqualTo(Uri.parse(newsItems.first().originUrl))

        Intents.release()
    }
}