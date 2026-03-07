package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jht.pointy.data.model.Course
import com.jht.pointy.data.network.ApiService
import com.jht.pointy.data.network.RetrofitClient
import com.jht.pointy.state.CourseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseViewModel : ViewModel() {

    private val api = RetrofitClient.instance.create(ApiService::class.java)

    private val _uiState = MutableStateFlow<CourseState>(CourseState.Idle)
    val uiState: StateFlow<CourseState> = _uiState

    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> = _selectedCourse

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _uiState.value = CourseState.Loading
            try {
                val courses = api.getMyCourses()
                _uiState.value = CourseState.Success(courses)
            } catch (e: retrofit2.HttpException) {
                val message = e.response()?.errorBody()?.string() ?: "Erreur de chargement"
                _uiState.value = CourseState.Error(message)
            } catch (e: Exception) {
                _uiState.value = CourseState.Error("Impossible de contacter le serveur")
            }
        }
    }

    fun loadCourseById(courseId: String) {
        viewModelScope.launch {
            try {
                val course = api.getCourseById(courseId)
                _selectedCourse.value = course
            } catch (e: Exception) {
                // TODO: gérer l'erreur
            }
        }
    }

    fun updateAttendance(courseId: String, studentId: String, attendance: String) {
        viewModelScope.launch {
            try {
                api.updateAttendance(courseId, studentId, attendance)
                // Ici, on recharge les cours pour avoir les présences à jour
                loadCourses()
            } catch (e: Exception) {
                // TODO: gérer l'erreur
            }
        }
    }
}