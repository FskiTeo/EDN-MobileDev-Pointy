package com.jht.pointy.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val token: String,
    val teacher: Teacher
)

data class Teacher(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String
)