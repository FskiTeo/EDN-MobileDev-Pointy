package com.jht.pointy.data.network

import com.jht.pointy.data.model.Course
import com.jht.pointy.data.model.LoginRequest
import com.jht.pointy.data.model.LoginResponse
import com.jht.pointy.data.model.Teacher
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("teachers/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("teachers/me")
    suspend fun getMe(): Teacher

    @GET("courses/mycourses")
    suspend fun getMyCourses(): List<Course>

    @GET("courses/{id}")
    suspend fun getCourseById(@Path("id") courseId: String): Course

    @PATCH("courses/presence/{courseId}/{studentId}/{attendance}")
    suspend fun updateAttendance(
        @Path("courseId") courseId: String,
        @Path("studentId") studentId: String,
        @Path("attendance") attendance: String
    )

}