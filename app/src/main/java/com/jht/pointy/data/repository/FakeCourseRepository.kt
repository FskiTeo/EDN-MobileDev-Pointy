package com.jht.pointy.data.repository

import com.jht.pointy.data.model.Course
import com.jht.pointy.data.model.Student
import java.time.LocalDateTime
import java.time.Duration

class FakeCourseRepository {
    fun getFakeCourses(): List<Course> = listOf(
        Course(
            id = "1",
            teacherId = "T1",
            name = "Développement Mobile",
            startDateTime = LocalDateTime.of(2026, 3, 2, 8, 30),
            duration = Duration.ofHours(2),
            location = "Salle A2"
        ),
        Course(
            id = "2",
            teacherId = "T1",
            name = "Architecture Logicielle",
            startDateTime = LocalDateTime.of(2026, 3, 2, 10, 30),
            duration = Duration.ofHours(2),
            location = "Amphi B"
        )
    )

    fun getFakeStudents(): List<Student> = listOf(
        Student(
            id = "101",
            firstName = "Alice",
            lastName = "Martin",
            nfcUid = "04:A1:B2:C3"
        ),
        Student( // Lui n'a pas encore sa carte enregistrée
            id = "102",
            firstName = "Bob",
            lastName = "Durand",
            nfcUid = null
        ),
        Student(
            id = "103",
            firstName = "Charlie",
            lastName = "Lefevre",
            nfcUid = "04:D4:E5:F6"
        )
    )

    fun getStudentsByCourse(courseId: String): List<Student> {
        return getFakeStudents() // pour l'instant on renvoie tout mais on remplacera par l'api backend
    }


}