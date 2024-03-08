package com.puras.itoandroidassignment.presentation.feed

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.puras.itoandroidassignment.R
import com.puras.itoandroidassignment.presentation.destinations.EntryScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@RootNavGraph(start = true)
@Destination
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FeedScreen(
    navigator: DestinationsNavigator,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val state = viewModel.stateFlow.collectAsState()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT

    /* The code below or init block inside a viewmodel pretty much does the same thing
    * except the code below also gets triggered when a recomposition happens. */
//    LaunchedEffect(key1 = true) {
//        Timber.d("OnInit")
//        viewModel.handleAction(FeedAction.OnInit)
//    }

    if (state.value.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp)
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize(),
    ) {

        LaunchedEffect(Unit) {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventFlow.collectLatest { event ->
                    when (event) {
                        FeedEvent.DisplayUnknownErrorMessage -> {
                            snackbarHostState.showSnackbar(
                                message = context.resources.getString(R.string.error_unknown),
                                duration = SnackbarDuration.Short
                            )
                        }

                        is FeedEvent.NavigateToEntryScreen -> {
                            navigator.navigate(EntryScreenDestination(event.url))
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = if (isPortrait) Alignment.Start else Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth(),
                    value = state.value.user,
                    onValueChange = {
                        lifecycleOwner.lifecycleScope.launch {
                            viewModel.handleAction(FeedAction.UserInputUpdated(it))
                        }
                    },
                    isError = state.value.isUserError,
                    supportingText = {
                        Text(
                            text = if (state.value.isUserError) stringResource(id = R.string.error_input) else "",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    singleLine = true,
                    label = { Text("User") }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth(),
                    value = state.value.repo,
                    onValueChange = {
                        lifecycleOwner.lifecycleScope.launch {
                            viewModel.handleAction(FeedAction.RepoInputUpdated(it))
                        }
                    },
                    isError = state.value.isRepoError,
                    supportingText = {
                        Text(
                            text = if (state.value.isRepoError) stringResource(id = R.string.error_input) else "",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    singleLine = true,
                    label = { Text("Repo") }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth(),
                    value = state.value.category,
                    onValueChange = {
                        lifecycleOwner.lifecycleScope.launch {
                            viewModel.handleAction(FeedAction.CategoryInputUpdated(it))
                        }
                    },
                    isError = state.value.isCategoryError,
                    supportingText = {
                        Text(
                            text = if (state.value.isCategoryError) stringResource(id = R.string.error_input) else "",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    singleLine = true,
                    label = { Text("Category") }
                )
            }

            /* Makes the design instantly more beautiful */
            Divider(thickness = 2.dp, color = MaterialTheme.colorScheme.primary)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = if (isPortrait) Alignment.Start else Alignment.CenterHorizontally
            ) {
                state.value.data.forEach { feed ->
                    FeedItem(
                        link = feed.link,
                        onClick = { url ->
                            lifecycleOwner.lifecycleScope.launch {
                                viewModel.handleAction(FeedAction.FeedSelected(url))
                            }
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun FeedItem(
    link: String,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .widthIn(max = 500.dp)
            .fillMaxWidth()
            .border(BorderStroke(2.dp, SolidColor(MaterialTheme.colorScheme.primary)))
            .clickable {
                onClick(link)
            }
    ) {
        Text(
            text = link,
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 4.dp)
                .fillMaxWidth(),
            fontSize = 14.sp,
            lineHeight = 18.sp
        )
    }
}

@Preview(name = "Feed Item Preview", device = "spec:width=411dp,height=891dp")
@Composable
fun FeedItemPreview() {
    FeedItem(
        "https://github.com/security-advisories",
        {}
    )
}
