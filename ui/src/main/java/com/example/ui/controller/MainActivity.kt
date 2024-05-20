package com.example.ui.controller

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.example.domain.model.NewsError
import com.example.ui.R
import com.example.ui.databinding.ActivityMainBinding
import com.example.ui.model.NewsItemUiState
import com.example.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

@AndroidEntryPoint
@OptionalInject
class MainActivity : AppCompatActivity() {

    private lateinit var layout: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        layout = DataBindingUtil.setContentView(this,R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup rv
        val newsAdapter = NewsListAdapter(::onNewsItemClick)
        layout.newsList.adapter = newsAdapter

        // Observe ui state changes from view model
        viewModel.uiState.observe(this) { state ->
            // Update news adapter
            newsAdapter.submitList(state.items)

            // Update progress bar
            showProgressBar(state.isLoading)

            // Handle error state
            state.error?.let { error -> showErrorMessage(error) }
        }
    }

    private fun showProgressBar(show: Boolean) {
        layout.progressBar.visibility = when(show) {
            true -> View.VISIBLE
            false -> View.GONE
        }
    }

    private fun showErrorMessage(error: NewsError) {
        val snackbar = when(error) {
            NewsError.Internal -> Snackbar.make(
                layout.main,
                getString(R.string.error_feature_unavailable),
                Snackbar.LENGTH_INDEFINITE)

            NewsError.DeviceNetwork -> Snackbar.make(
                layout.main,
                getString(R.string.error_device_not_connected),
                Snackbar.LENGTH_INDEFINITE)

            NewsError.RemoteServer -> Snackbar.make(
                layout.main,
                getString(R.string.error_feature_unavailable),
                Snackbar.LENGTH_INDEFINITE)
        }

        snackbar.setAction(getString(R.string.action_retry)) { viewModel.reloadNews() }
        snackbar.show()
    }

    private fun onNewsItemClick(item: NewsItemUiState) {
        openArticleInDeviceApp(item.originUrl)
    }

    private fun openArticleInDeviceApp(url: String) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        val chooser = Intent.createChooser(webIntent, getString(R.string.title_read_article))

        startActivity(chooser)
    }
}