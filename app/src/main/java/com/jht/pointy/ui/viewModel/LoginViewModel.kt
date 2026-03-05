package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jht.pointy.data.network.ApiConfig
import com.jht.pointy.state.LoginState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState.Idle)
    val uiState: StateFlow<LoginState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginState.Loading

            val result = withContext(Dispatchers.IO) {
                try {
                    val connection = (URL(ApiConfig.LOGIN).openConnection() as HttpURLConnection).apply {
                        requestMethod = "POST"
                        setRequestProperty("Content-Type", "application/json")
                        doOutput       = true
                        connectTimeout = 10_000
                        readTimeout    = 10_000
                    }

                    val body = JSONObject().apply {
                        put("email", email)
                        put("password", password)
                    }.toString()

                    OutputStreamWriter(connection.outputStream).use { it.write(body) }

                    val responseCode = connection.responseCode

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        val responseText = BufferedReader(InputStreamReader(connection.inputStream))
                            .use { it.readText() }
                        connection.disconnect()

                        val json = JSONObject(responseText)
                        LoginState.Success

                    } else {
                        val errorText = connection.errorStream?.let {
                            BufferedReader(InputStreamReader(it)).use { r -> r.readText() }
                        } ?: "Erreur serveur ($responseCode)"
                        connection.disconnect()

                        val message = try {
                            JSONObject(errorText).optString("message", "Identifiants incorrects")
                        } catch (e: Exception) {
                            "Identifiants incorrects"
                        }
                        LoginState.Error(message)
                    }

                } catch (e: Exception) {
                    LoginState.Error("Impossible de contacter le serveur : ${e.message}")
                }
            }

            _uiState.value = result
        }
    }
}