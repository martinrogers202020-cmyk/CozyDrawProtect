package com.cozyprotect.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cozyprotect.LevelPack
import com.cozyprotect.LevelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface StageSelectUiState {
    object Loading : StageSelectUiState
    data class Loaded(val packs: List<LevelPack>) : StageSelectUiState
    data class Error(val message: String) : StageSelectUiState
}

class StageSelectViewModel(private val repository: LevelRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<StageSelectUiState>(StageSelectUiState.Loading)
    val uiState: StateFlow<StageSelectUiState> = _uiState.asStateFlow()

    private val _isGrid = MutableStateFlow(false)
    val isGrid: StateFlow<Boolean> = _isGrid.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = StageSelectUiState.Loading
        viewModelScope.launch {
            runCatching { repository.loadAllPacks() }
                .onSuccess { packs -> _uiState.value = StageSelectUiState.Loaded(packs) }
                .onFailure { error -> _uiState.value = StageSelectUiState.Error(error.message ?: "Could not load packs") }
        }
    }

    fun downloadNewPack(url: String) {
        _uiState.value = StageSelectUiState.Loading
        repository.downloadRemotePack(url) {
            viewModelScope.launch {
                refresh()
            }
        }
    }

    fun toggleGrid() {
        _isGrid.value = !_isGrid.value
    }
}
