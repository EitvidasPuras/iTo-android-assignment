package com.puras.itoandroidassignment.presentation.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.domain.use_case.GetFeedsUseCase
import com.puras.itoandroidassignment.util.REPO_DISCUSSIONS
import com.puras.itoandroidassignment.util.REPO_DISCUSSIONS_CATEGORY
import com.puras.itoandroidassignment.util.Resource
import com.puras.itoandroidassignment.util.USER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class FeedViewState(
    val data: List<Feed> = emptyList(),
    val user: String = "",
    val isUserError: Boolean = false,
    val repo: String = "",
    val isRepoError: Boolean = false,
    val category: String = "",
    val isCategoryError: Boolean = false,
    val isLoading: Boolean = true
)

sealed interface FeedAction {
    data object OnInit : FeedAction
    data class FeedSelected(val feed: Feed) : FeedAction
    data class UserInputUpdated(val text: String) : FeedAction
    data class RepoInputUpdated(val text: String) : FeedAction
    data class CategoryInputUpdated(val text: String) : FeedAction
}

sealed interface FeedEvent {
    data class NavigateToEntryScreen(val feed: Feed) : FeedEvent
    data object DisplayUnknownErrorMessage : FeedEvent
}

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedsUseCase: GetFeedsUseCase,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow(FeedViewState())
    val stateFlow = _stateFlow.asStateFlow()

    private val _eventFlow = MutableSharedFlow<FeedEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        Timber.d("OnInit")
        handleInit()
    }

    suspend fun handleAction(action: FeedAction) {
        when (action) {
            FeedAction.OnInit -> handleInit()
            is FeedAction.FeedSelected -> handleFeedSelection(action.feed)
            is FeedAction.UserInputUpdated -> _stateFlow.update { it.copy(user = action.text) }
            is FeedAction.RepoInputUpdated -> _stateFlow.update { it.copy(repo = action.text) }
            is FeedAction.CategoryInputUpdated -> _stateFlow.update { it.copy(category = action.text) }
        }
    }

    /* Either check by feed.key or feed.url.constans({user}), feed.url.constains({repo}), etc. */
    private suspend fun handleFeedSelection(feed: Feed) {
        when (feed.key) {
            REPO_DISCUSSIONS_CATEGORY -> handleRepoDiscussionsCategory(feed)
            REPO_DISCUSSIONS -> handleRepoDiscussions(feed)
            USER -> handleUser(feed)
            else -> _eventFlow.emit(FeedEvent.NavigateToEntryScreen(feed))
        }
    }

    private suspend fun handleUser(feed: Feed) {
        /* Clear any errors from the previous feed selection attempts */
        if (stateFlow.value.isRepoError || stateFlow.value.isCategoryError) {
            clearErrors(isRepoError = false, isCategoryError = false)
        }
        /* Since user input is the only one needed - validate it */
        validateUserInput()
        /* If there are any errors - quit */
        if (stateFlow.value.isUserError) return
        /* If there are no errors - open the display screen */
        _eventFlow.emit(
            FeedEvent.NavigateToEntryScreen(
                feed.copy(
                    link = feed.link.replace("{user}", stateFlow.value.user.trim())
                )
            )
        )
    }

    private suspend fun handleRepoDiscussions(feed: Feed) {
        /* Clear any errors from the previous feed selection attempts */
        if (stateFlow.value.isCategoryError) clearErrors(isCategoryError = false)
        /* Validate the only two inputs that are necessary */
        validateUserInput()
        validateRepoInput()
        /* If any of the inputs have errors - quit */
        if (stateFlow.value.isUserError || stateFlow.value.isRepoError) return
        /* If the inputs are good - let's goooooo */
        _eventFlow.emit(
            FeedEvent.NavigateToEntryScreen(
                feed.copy(
                    link = feed.link.replace("{user}", stateFlow.value.user.trim())
                        .replace("{repo}", stateFlow.value.repo.trim())
                )
            )
        )
    }

    private suspend fun handleRepoDiscussionsCategory(feed: Feed) {
        /* Validate the necessary inputs */
        validateUserInput()
        validateRepoInput()
        validateCategoryInput()
        /* If any of the inputs have errors - quit */
        if (stateFlow.value.isUserError || stateFlow.value.isRepoError || stateFlow.value.isCategoryError) return
        /* If the inputs are good - we happy */
        _eventFlow.emit(
            FeedEvent.NavigateToEntryScreen(
                feed.copy(
                    link = feed.link.replace("{user}", stateFlow.value.user.trim())
                        .replace("{repo}", stateFlow.value.repo.trim())
                        .replace("{category}", stateFlow.value.category.trim())
                )
            )
        )
    }

    private fun clearErrors(
        isUserError: Boolean? = null,
        isRepoError: Boolean? = null,
        isCategoryError: Boolean? = null
    ) {
        _stateFlow.update { state ->
            state.copy(
                isUserError = isUserError ?: state.isUserError,
                isRepoError = isRepoError ?: state.isRepoError,
                isCategoryError = isCategoryError ?: state.isCategoryError
            )
        }
    }

    private fun validateUserInput() {
        if (stateFlow.value.user.isEmpty()) {
            _stateFlow.update { it.copy(isUserError = true) }
        } else {
            _stateFlow.update { it.copy(isUserError = false) }
        }
    }

    private fun validateRepoInput() {
        if (stateFlow.value.repo.isEmpty()) {
            _stateFlow.update { it.copy(isRepoError = true) }
        } else {
            _stateFlow.update { it.copy(isRepoError = false) }
        }
    }

    private fun validateCategoryInput() {
        if (stateFlow.value.category.isEmpty()) {
            _stateFlow.update { it.copy(isCategoryError = true) }
        } else {
            _stateFlow.update { it.copy(isCategoryError = false) }
        }
    }

    private fun handleInit() = viewModelScope.launch(Dispatchers.IO) {
        _stateFlow.update { it.copy(isLoading = true) }
        when (val response = getFeedsUseCase()) {
            is Resource.Success -> {
                _stateFlow.update {
                    it.copy(data = response.data, isLoading = false)
                }
            }

            is Resource.Error -> {
                _stateFlow.update { it.copy(isLoading = false) }
                _eventFlow.emit(FeedEvent.DisplayUnknownErrorMessage)
            }
        }
    }
}