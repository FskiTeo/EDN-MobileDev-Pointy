package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jht.pointy.data.model.Course
import com.jht.pointy.data.model.toCourse
import com.jht.pointy.data.network.ApiService
import com.jht.pointy.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    data class Success(val courses: List<Course>) : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}

class DashboardViewModel : ViewModel() {

    private val api = RetrofitClient.instance.create(ApiService::class.java)

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                val courses = api.getMyCourses().map { it.toCourse() }
                _uiState.value = DashboardUiState.Success(courses)
            } catch (_: Exception) {
                _uiState.value = DashboardUiState.Error("Impossible de charger les cours")
            }
        }
    }

    fun getCourses(): List<Course> {
        return (uiState.value as? DashboardUiState.Success)?.courses ?: emptyList()
    }
}