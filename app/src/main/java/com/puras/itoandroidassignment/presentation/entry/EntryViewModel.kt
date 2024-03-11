package com.puras.itoandroidassignment.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puras.itoandroidassignment.data.local.model.Entry
import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.domain.use_case.GetEntriesUseCase
import com.puras.itoandroidassignment.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EntryViewState(
    val data: List<Entry> = emptyList(),
    val isLoading: Boolean = true
)

sealed interface EntryAction {

    data class OnInit(val feed: Feed) : EntryAction
}

sealed interface EntryEvent {

}

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val getEntriesUseCase: GetEntriesUseCase
) : ViewModel() {

    private val _stateFlow = MutableStateFlow(EntryViewState())
    val stateFlow = _stateFlow.asStateFlow()

    private val _eventFlow = MutableSharedFlow<EntryEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    suspend fun handleAction(action: EntryAction) {
        when (action) {
            is EntryAction.OnInit -> handleInit(action.feed)
        }
    }

    private fun handleInit(feed: Feed) = viewModelScope.launch(Dispatchers.IO) {
        _stateFlow.update { it.copy(isLoading = true) }
        handleTimelineFeed(feed)
//        when (feed.key) {
//            REPO_DISCUSSIONS_CATEGORY -> handleRepoDiscussionsCategory(feed)
//            REPO_DISCUSSIONS -> handleRepoDiscussions(feed)
//            USER -> handleUser(feed)
//            TIMELINE ->
//            SEC_ADVISORIES ->
//        }
    }

    private suspend fun handleTimelineFeed(feed: Feed) {
        when (val response = getEntriesUseCase(feed)) {
            is Resource.Success -> {
                _stateFlow.update {
                    it.copy(data = response.data, isLoading = false)
                }
            }

            is Resource.Error -> {
                _stateFlow.update { it.copy(isLoading = false) }
//                _eventFlow.emit(EntryEvent.DisplayUnknownErrorMessage)
            }
        }
    }

}