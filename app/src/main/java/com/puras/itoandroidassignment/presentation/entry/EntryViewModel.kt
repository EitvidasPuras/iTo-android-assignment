package com.puras.itoandroidassignment.presentation.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puras.itoandroidassignment.data.local.model.Entry
import com.puras.itoandroidassignment.data.local.model.Feed
import com.puras.itoandroidassignment.domain.use_case.GetEntriesUseCase
import com.puras.itoandroidassignment.util.ErrorType
import com.puras.itoandroidassignment.util.NetworkStatusTracker
import com.puras.itoandroidassignment.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    data object DisplayNetworkCallErrorMessage : EntryEvent
    data object DisplayNotFoundError : EntryEvent
}

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val getEntriesUseCase: GetEntriesUseCase,
    private val networkStatusTracker: NetworkStatusTracker,
) : ViewModel() {

    private val _stateFlow = MutableStateFlow(EntryViewState())
    val stateFlow = _stateFlow.asStateFlow()

    private val _eventFlow = MutableSharedFlow<EntryEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    suspend fun handleAction(action: EntryAction) {
        when (action) {
            is EntryAction.OnInit -> {
                trackNetworkStatus(action.feed)
                getEntries(action.feed)
            }
        }
    }

    private fun trackNetworkStatus(feed: Feed) = viewModelScope.launch {
        networkStatusTracker.isConnectedFlow.collectLatest {
            if (stateFlow.value.data.isEmpty() && !stateFlow.value.isLoading && it) {
                getEntries(feed)
            }
        }
    }

    private fun getEntries(feed: Feed) = viewModelScope.launch(Dispatchers.IO) {
        _stateFlow.update { it.copy(isLoading = true) }
        when (val response = getEntriesUseCase(feed)) {
            is Resource.Success -> {
                _stateFlow.update {
                    it.copy(data = response.data, isLoading = false)
                }
            }

            is Resource.Error -> {
                _stateFlow.update { it.copy(isLoading = false) }
                when (response.errorType) {
                    ErrorType.NOT_FOUND -> _eventFlow.emit(EntryEvent.DisplayNotFoundError)
                    ErrorType.UNKNOWN -> _eventFlow.emit(EntryEvent.DisplayNetworkCallErrorMessage)
                }


            }
        }
    }

}