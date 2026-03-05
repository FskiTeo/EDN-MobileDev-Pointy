package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import com.jht.pointy.data.model.Student
import com.jht.pointy.data.repository.FakeCourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CourseViewModel : ViewModel() {
    private val repository = FakeCourseRepository()

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    fun loadStudents(courseId: String) {
        _students.value = repository.getStudentsByCourse(courseId)
    }

    fun markAsPresent(uid: String) {
        _students.value = _students.value.map {
            if (it.nfcUid == uid) it.copy(isPresent = true) else it
        }
    }
}