package com.jht.pointy.data.model

data class Student(
    val id: String,
    val firstName: String,
    val nfcUid: String? = null,
    val isPresent: Boolean = false
)