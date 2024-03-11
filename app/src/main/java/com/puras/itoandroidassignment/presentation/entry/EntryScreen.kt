package com.puras.itoandroidassignment.presentation.entry

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.puras.itoandroidassignment.R
import com.puras.itoandroidassignment.data.local.model.Entry
import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.presentation.ui.util.CacheImage
import com.puras.itoandroidassignment.presentation.ui.util.CircularIndeterminateIndicator
import com.puras.itoandroidassignment.presentation.ui.util.ScreenMessage
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun EntryScreen(
    viewModel: EntryViewModel = hiltViewModel(),
    feed: Feed,
) {
    LaunchedEffect(key1 = true) {
        Timber.d("EntryScreen init")
        viewModel.handleAction(EntryAction.OnInit(feed))
    }
    val state = viewModel.stateFlow.collectAsState()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    snackbarData = it
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        LaunchedEffect(Unit) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collectLatest { event ->
                    when (event) {
                        EntryEvent.DisplayNetworkCallErrorMessage -> {
                            snackbarHostState.showSnackbar(
                                message = context.resources.getString(R.string.error_unknown),
                                duration = SnackbarDuration.Short
                            )
                        }

                        EntryEvent.DisplayNotFoundError -> {
                            snackbarHostState.showSnackbar(
                                message = context.resources.getString(R.string.error_not_found),
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            }
        }

        if (state.value.isLoading) {
            CircularIndeterminateIndicator()
        }

        if (!state.value.isLoading && state.value.data.isEmpty()) {
            ScreenMessage(
                title = stringResource(id = R.string.message_no_data_title),
                subtitle = stringResource(id = R.string.message_no_data_subtitle)
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                content = {
                    items(state.value.data) { entry ->
                        entry.title?.let { title(title = it, isPortrait = isPortrait) }
                        entry.author?.let { author(entry = entry, isPortrait = isPortrait) }
                        entry.published?.let { published(date = it) }
                        entry.content?.let { webView(content = it) }
                        if (entry.title != null && entry.author != null && entry.published != null && entry.content != null) {
                            Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun title(
    title: String,
    isPortrait: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isPortrait) Arrangement.Start else Arrangement.Center,
    ) {
        Text(
            text = title.trim(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            maxLines = if (isPortrait) 2 else 4,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun author(
    entry: Entry,
    isPortrait: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (isPortrait) 10.dp else 16.dp)
    ) {
        /* No point in displaying an image without an author */
        if (entry.media != null) {
            CacheImage(
                modifier = Modifier.size(40.dp),
                url = entry.media
            )
        }
        Text(
            text = stringResource(
                id = R.string.text_author_by,
                entry.author!!
            ),
            style = MaterialTheme.typography.headlineSmall,
            maxLines = if (isPortrait) 1 else 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun published(
    date: String
) {
    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = date,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun webView(
    content: String
) {
    AndroidView(
        modifier = Modifier.padding(top = 16.dp),
        factory = {
            val wv = WebView(it)
            wv.settings.domStorageEnabled = true
            wv.settings.setSupportZoom(false)
            /* Prevent further navigation in a WebView */
            wv.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return true
                }
            }
            wv.isScrollContainer = false
            wv.isHorizontalScrollBarEnabled = false
            wv.isVerticalScrollBarEnabled = false
            wv
            /* Couldn't decide which one looks better */
//            TextView(it)
        },
        update = {
            it.loadData(content, "text/html", "UTF-8")
//            it.text = HtmlCompat.fromHtml(
//                content,
//                HtmlCompat.FROM_HTML_MODE_COMPACT
//            )
        }
    )
}