package com.jht.pointy.data.model

data class Student(
    val id: String,
    val firstName: String,
    val lastName: String,
    val nfcUid: String? = null,
    val attendance: String = "absent"
) {
    val isPresent: Boolean
        get() = attendance.equals("present", ignoreCase = true)
}
