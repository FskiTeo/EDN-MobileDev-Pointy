package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jht.pointy.data.model.Student
import com.jht.pointy.data.model.toStudents
import com.jht.pointy.data.network.ApiService
import com.jht.pointy.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseViewModel : ViewModel() {
    private val api = RetrofitClient.instance.create(ApiService::class.java)

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadStudents(courseId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val course = api.getCourseById(courseId)
                _students.value = course.toStudents()
            } catch (_: Exception) {
                _students.value = emptyList()
                _errorMessage.value = "Impossible de charger les élèves"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsPresent(uid: String) {
        _students.value = _students.value.map {
            if (it.nfcUid == uid) it.copy(isPresent = true) else it
        }
    }
}