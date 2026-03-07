package com.jht.pointy.data.model

data class Course(
    val id: String,
    val teacherId: String,
    val name: String,
    val startDateTime: String,
    val duration: Int,
    val location: String,
    val studentCount: Int? = null,
    val courseStudents: List<CourseStudent>? = null
)

data class CourseStudent(
    val courseId: String,
    val studentId: String,
    val attendance: String,
    val student: Student
)