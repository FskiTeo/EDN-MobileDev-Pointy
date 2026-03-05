package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jht.pointy.state.LoginState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading

            delay(2000)

            if (email == "admin@univ.fr" && pass == "1234") {
                _uiState.value = LoginState.Success
            } else {
                _uiState.value = LoginState.Error("Identifiants incorrects")
            }
        }
    }
}