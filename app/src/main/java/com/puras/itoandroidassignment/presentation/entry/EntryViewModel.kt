package com.puras.itoandroidassignment.presentation.entry

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class EntryViewState(
    val isLoading: Boolean = false
)

sealed interface EntryAction {

}

sealed interface EntryEvent {

}

@HiltViewModel
class EntryViewModel @Inject constructor(

) : ViewModel() {

    private val _stateFlow = MutableStateFlow(EntryViewState())
    val stateFlow = _stateFlow.asStateFlow()

    private val _eventFlow = MutableSharedFlow<EntryEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {

    }

    suspend fun handleAction(action: EntryAction) {
//        when (action) {
//            FeedAction.OnInit -> handleInit()
//            is FeedAction.FeedSelected -> handleFeedSelection(action.url)
//            is FeedAction.UserInputUpdated -> _stateFlow.update { it.copy(user = action.text) }
//            is FeedAction.RepoInputUpdated -> _stateFlow.update { it.copy(repo = action.text) }
//            is FeedAction.CategoryInputUpdated -> _stateFlow.update { it.copy(category = action.text) }
//        }
    }

}