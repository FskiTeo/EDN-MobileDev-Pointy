package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jht.pointy.data.model.CourseAttendancePatchRequest
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

    private val _updatingStudentId = MutableStateFlow<String?>(null)
    val updatingStudentId: StateFlow<String?> = _updatingStudentId

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
            if (it.nfcUid == uid) it.copy(attendance = "present") else it
        }
    }

    fun rotateAttendance(courseId: String, student: Student) {
        val nextAttendance = nextAttendance(student.attendance)
        viewModelScope.launch {
            _updatingStudentId.value = student.id
            _errorMessage.value = null
            try {
                api.patchAttendance(
                    CourseAttendancePatchRequest(
                        courseId = courseId,
                        studentId = student.id,
                        attendance = nextAttendance
                    )
                )

                _students.value = _students.value.map {
                    if (it.id == student.id) it.copy(attendance = nextAttendance) else it
                }
            } catch (_: Exception) {
                _errorMessage.value = "Impossible de mettre à jour la présence"
            } finally {
                _updatingStudentId.value = null
            }
        }
    }

    private fun nextAttendance(current: String): String {
        return when (current.lowercase()) {
            "absent" -> "present"
            "present" -> "excused"
            else -> "absent"
        }
    }
}