package com.jht.pointy.ui.dashboard

import androidx.lifecycle.ViewModel
import com.jht.pointy.data.model.Course
import com.jht.pointy.data.repository.FakeCourseRepository

class DashboardViewModel : ViewModel() {

    private val repository = FakeCourseRepository()

    fun getCourses(): List<Course> {
        return repository.getFakeCourses()
    }
}