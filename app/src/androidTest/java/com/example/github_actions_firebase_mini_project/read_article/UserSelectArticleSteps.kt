package com.example.github_actions_firebase_mini_project.read_article

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until.hasObject
import com.example.data.remote.FEEDLY_FEED_PATH
import com.google.common.truth.Truth
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import java.io.BufferedReader
import java.io.InputStreamReader

class UserSelectArticleSteps(server: MockWebServer) : GreenCoffeeSteps(){

    private val newsItemTitle = "New Teslas might lose Steam"
    private val newsItemUrl = "https://www.theverge.com/2024/5/17/24158929/tesla-steam-discontinue-new-model-x-delivery-s-cybertruck"

    init {
        val dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val feedPath = "/$FEEDLY_FEED_PATH"
                val jsonPath = "assets/json/news.json"

                return when(request.requestUrl.uri().path) {
                    feedPath -> MockResponse()
                        .setBody(readStringFromFile(jsonPath))
                        .setResponseCode(200)

                    else -> MockResponse().setResponseCode(404)
                }
            }
        }

        server.setDispatcher(dispatcher)
    }

    @Given("^user on device home screen$")
    fun userOnDeviceHomeScreen() {
        openDeviceHome()
    }

    @When("^he opens app$")
    fun heOpenApp() {
        launchApp()
    }

    @And("^selects article from news screen$")
    fun selectArticleFromNews() {
        Intents.init()
        onView(withText(newsItemTitle))
            .perform(click())
    }

    @Then("^user should select device app for article reading$")
    fun openArticleFromDeviceApp() {
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_CHOOSER))

        val intent = Intents.getIntents().first().extras?.get(Intent.EXTRA_INTENT) as Intent

        Truth.assertThat(intent.action).isEqualTo(Intent.ACTION_VIEW)
        Truth.assertThat(intent.data).isEqualTo(Uri.parse(newsItemUrl))

        Intents.release()
    }

    private fun readStringFromFile(resourceName: String): String {
        val stream = javaClass.classLoader!!.getResourceAsStream(resourceName)
        val reader = BufferedReader(InputStreamReader(stream))
        val builder = StringBuilder()
        var line: String?

        while (reader.readLine().also { line = it } != null) {
            builder.append(line)
        }

        reader.close()
        return builder.toString()
    }

    private fun openDeviceHome() {
        UiDevice.getInstance(getInstrumentation()).pressHome()
    }

    fun launchApp() {
        val timeout = 5000L
        val launcherPackage =
            getLaunchPackageName()
        assertThat(launcherPackage, notNullValue())
        UiDevice.getInstance(getInstrumentation())
            .wait(hasObject(By.pkg(launcherPackage).depth(0)), timeout)

        // Launch the blueprint app
        val context = getApplicationContext<Context>()
        val appPackage = context.packageName
        val intent = context.packageManager
            .getLaunchIntentForPackage(appPackage)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)    // Clear out any previous instances
        context.startActivity(intent)

        // Wait for the app to appear
        UiDevice.getInstance(getInstrumentation())
            .wait(hasObject(By.pkg(appPackage).depth(0)), timeout)
    }

    private fun getLaunchPackageName(): String {
        // Create launcher Intent
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)

        // Use PackageManager to get the launcher package name
        val pm = getInstrumentation().context.packageManager
        val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)

        return resolveInfo!!.activityInfo.packageName
    }
}