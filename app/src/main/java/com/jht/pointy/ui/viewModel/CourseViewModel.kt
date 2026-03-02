package com.jht.pointy.ui.viewModel

import androidx.lifecycle.ViewModel
import com.jht.pointy.model.Student
import com.jht.pointy.repository.FakeCourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CourseViewModel : ViewModel() {
    private val repository = FakeCourseRepository()

    private val _students = MutableStateFlow(repository.getFakeStudents())
    val students: StateFlow<List<Student>> = _students

    fun markAsPresent(uid: String) {
        _students.value = _students.value.map {
            if (it.nfcUid == uid) it.copy(isPresent = true) else it
        }
    }
}