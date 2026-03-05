package com.jht.pointy.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private val httpClient = OkHttpClient.Builder()
        // Interceptor qui ajoute automatiquement le token Bearer sur chaque requête
        .addInterceptor { chain ->
            val original = chain.request()
            val token = SessionManager.token

            val request = if (token != null) {
                original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                original
            }
            chain.proceed(request)
        }
        .build()

    val instance: Retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL + "/")
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}