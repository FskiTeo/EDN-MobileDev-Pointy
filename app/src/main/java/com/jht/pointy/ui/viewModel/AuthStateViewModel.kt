package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jht.pointy.data.network.ApiService
import com.jht.pointy.data.network.RetrofitClient
import com.jht.pointy.data.network.SessionManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException

enum class AuthUiState {
    Checking,
    LoggedOut,
    LoggedIn
}

class AuthStateViewModel : ViewModel() {

    private val api = RetrofitClient.instance.create(ApiService::class.java)

    private val _authState = MutableStateFlow(AuthUiState.Checking)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            unauthorizedEvents.collectLatest {
                SessionManager.clearSession()
                _authState.value = AuthUiState.LoggedOut
            }
        }
    }

    fun bootstrapSession() {
        if (_authState.value != AuthUiState.Checking) return

        viewModelScope.launch {
            val persistedToken = SessionManager.hydrateTokenFromStorage()
            if (persistedToken.isNullOrBlank()) {
                _authState.value = AuthUiState.LoggedOut
                return@launch
            }

            try {
                api.getMyCourses()
                if (SessionManager.token == persistedToken) {
                    val teacher = api.getMe()
                    SessionManager.updateTeacherIdentity(
                        teacherId = teacher.id,
                        teacherFirstName = teacher.firstName,
                        teacherLastName = teacher.lastName
                    )
                    _authState.value = AuthUiState.LoggedIn
                }
            } catch (_: HttpException) {
                if (SessionManager.token == persistedToken) {
                    SessionManager.clearSession()
                    _authState.value = AuthUiState.LoggedOut
                }
            } catch (_: Exception) {
                if (SessionManager.token == persistedToken) {
                    _authState.value = AuthUiState.LoggedOut
                }
            }
        }
    }

    fun onLoginSuccess() {
        _authState.value = AuthUiState.LoggedIn
    }

    companion object {
        private val unauthorizedEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

        fun notifyHttpError() {
            unauthorizedEvents.tryEmit(Unit)
        }
    }
}
