package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jht.pointy.data.network.ApiConfig
import com.jht.pointy.data.network.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

data class TeacherProfile(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String
)

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val teacher: TeacherProfile) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val uiState: StateFlow<ProfileState> = _uiState

    init {
        fetchProfile()
    }

    fun fetchProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileState.Loading

            val result = withContext(Dispatchers.IO) {
                try {
                    val token = SessionManager.token
                        ?: return@withContext ProfileState.Error("Non authentifié")

                    val connection = (URL(ApiConfig.ME).openConnection() as HttpURLConnection).apply {
                        requestMethod = "GET"
                        setRequestProperty("Authorization", "Bearer $token")
                        setRequestProperty("Content-Type", "application/json")
                        connectTimeout = 10_000
                        readTimeout    = 10_000
                    }

                    val responseCode = connection.responseCode

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val responseText = BufferedReader(InputStreamReader(connection.inputStream))
                            .use { it.readText() }
                        connection.disconnect()

                        val json = JSONObject(responseText)
                        ProfileState.Success(
                            TeacherProfile(
                                id        = json.getString("id"),
                                firstName = json.getString("firstName"),
                                lastName  = json.getString("lastName"),
                                email     = json.getString("email")
                            )
                        )
                    } else {
                        connection.disconnect()
                        ProfileState.Error("Erreur serveur ($responseCode)")
                    }

                } catch (e: Exception) {
                    ProfileState.Error("Impossible de contacter le serveur : ${e.message}")
                }
            }

            _uiState.value = result
        }
    }
}