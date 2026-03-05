package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jht.pointy.data.model.Teacher
import com.jht.pointy.data.network.ApiService
import com.jht.pointy.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val teacher: Teacher) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel : ViewModel() {

    private val api = RetrofitClient.instance.create(ApiService::class.java)

    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileState.Loading
            try {
                val teacher = api.getMe()
                _uiState.value = ProfileState.Success(teacher)
            } catch (e: Exception) {
                _uiState.value = ProfileState.Error("Impossible de charger le profil")
            }
        }
    }
}