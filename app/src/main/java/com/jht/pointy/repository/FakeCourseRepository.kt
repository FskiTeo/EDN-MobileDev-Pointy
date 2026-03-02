package com.jht.pointy.repository

import com.jht.pointy.model.Course
import com.jht.pointy.model.Student

class FakeCourseRepository {
    fun getFakeCourses(): List<Course> = listOf(
        Course("1", "Développement Mobile", "08:30", "Salle A2"),
        Course("2", "Architecture Logicielle", "10:30", "Amphi B")
    )

    fun getFakeStudents(): List<Student> = listOf(
        Student("101", "Alice", "04:A1:B2:C3"),
        Student("102", "Bob", null), // Lui n'a pas encore sa carte enregistrée
        Student("103", "Charlie", "04:D4:E5:F6")
    )
}