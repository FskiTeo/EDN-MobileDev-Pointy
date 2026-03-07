package com.jht.pointy.data.model

import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class CourseListItemDto(
    val id: String,
    val teacherId: String,
    val name: String,
    val startDateTime: String,
    val duration: Int,
    val location: String,
    val teacher: Teacher? = null,
    val studentCount: Int? = null
)

data class CourseDetailDto(
    val id: String,
    val teacherId: String,
    val name: String,
    val startDateTime: String,
    val duration: Int,
    val location: String,
    val teacher: Teacher? = null,
    val courseStudents: List<CourseStudentDto>? = null
)

data class CourseStudentDto(
    val studentId: String,
    val attendance: String,
    val student: StudentLiteDto
)

data class StudentLiteDto(
    val id: String,
    val firstName: String,
    val lastName: String
)

data class CourseAttendancePatchRequest(
    val courseId: String,
    val attendance: String,
    val studentId: String? = null,
    val cardSerial: String? = null
)

fun CourseListItemDto.toCourse(): Course {
    return Course(
        id = id,
        teacherId = teacherId,
        name = name,
        startDateTime = startDateTime.toLocalDateTimeOrNow(),
        duration = Duration.ofHours(duration.toLong()),
        location = location
    )
}

fun CourseDetailDto.toStudents(): List<Student> {
    return courseStudents.orEmpty().map { courseStudent ->
        Student(
            id = courseStudent.student.id,
            firstName = courseStudent.student.firstName,
            lastName = courseStudent.student.lastName,
            nfcUid = null,
            attendance = courseStudent.attendance.lowercase()
        )
    }
}

private fun String.toLocalDateTimeOrNow(): LocalDateTime {
    return try {
        LocalDateTime.ofInstant(Instant.parse(this), ZoneId.systemDefault())
    } catch (_: Exception) {
        LocalDateTime.now()
    }
}
