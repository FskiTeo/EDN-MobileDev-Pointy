package com.jht.pointy.data.network

import com.jht.pointy.data.model.LoginRequest
import com.jht.pointy.data.model.LoginResponse
import com.jht.pointy.data.model.Teacher
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("teachers/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("teachers/me")
    suspend fun getMe(): Teacher

}