package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jht.pointy.data.model.LoginRequest
import com.jht.pointy.data.network.ApiService
import com.jht.pointy.data.network.RetrofitClient
import com.jht.pointy.data.network.SessionManager
import com.jht.pointy.state.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val api = RetrofitClient.instance.create(ApiService::class.java)

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading
            try {
                val response = api.login(LoginRequest(email, password))
                SessionManager.token     = response.token
                SessionManager.teacherId = response.teacher.id
                SessionManager.teacherFirstName = response.teacher.firstName
                SessionManager.teacherLastName = response.teacher.lastName
                _uiState.value = LoginState.Success
            } catch (e: retrofit2.HttpException) {
                val message = e.response()?.errorBody()?.string() ?: "Identifiants incorrects"
                _uiState.value = LoginState.Error(message)
            } catch (e: Exception) {
                _uiState.value = LoginState.Error("Impossible de contacter le serveur")
            }
        }
    }
}