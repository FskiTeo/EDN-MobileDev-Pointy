package com.jht.pointy.data.network

import com.jht.pointy.data.model.LoginRequest
import com.jht.pointy.data.model.LoginResponse
import com.jht.pointy.data.model.Teacher
import com.jht.pointy.data.model.CourseDetailDto
import com.jht.pointy.data.model.CourseListItemDto
import com.jht.pointy.data.model.CourseAttendancePatchRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.POST

interface ApiService {

    @POST("teachers/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("teachers/me")
    suspend fun getMe(): Teacher

    @GET("courses/mycourses")
    suspend fun getMyCourses(): List<CourseListItemDto>

    @GET("courses/{id}")
    suspend fun getCourseById(@Path("id") courseId: String): CourseDetailDto

    @PATCH("courses/attendance")
    suspend fun patchAttendance(@Body request: CourseAttendancePatchRequest)

}