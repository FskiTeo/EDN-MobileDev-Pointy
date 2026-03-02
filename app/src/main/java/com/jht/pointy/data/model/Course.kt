package com.jht.pointy.data.model

import java.time.Duration
import java.time.LocalDateTime

data class Course(
    val id: String,
    val teacherId: String,
    val name: String,
    val startDateTime: LocalDateTime,
    val duration: Duration,
    val location: String
)