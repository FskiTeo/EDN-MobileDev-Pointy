package com.jht.pointy.state

import com.jht.pointy.data.model.Course

sealed class CourseState {
    object Idle : CourseState()
    object Loading : CourseState()
    data class Success(val courses: List<Course>) : CourseState()
    data class Error(val message: String) : CourseState()
}