package com.jht.pointy.data.network

import android.content.Context
import com.jht.pointy.data.session.SecureSessionStorage

object SessionManager {

    private var sessionStorage: SecureSessionStorage? = null

    var token: String? = null
        private set

    var teacherId: String? = null
        private set

    var teacherFirstName: String? = null
        private set

    var teacherLastName: String? = null
        private set

    fun initialize(context: Context) {
        if (sessionStorage == null) {
            sessionStorage = SecureSessionStorage(context.applicationContext)
        }
    }

    suspend fun setSession(
        token: String,
        teacherId: String,
        teacherFirstName: String,
        teacherLastName: String
    ) {
        this.token = token
        this.teacherId = teacherId
        this.teacherFirstName = teacherFirstName
        this.teacherLastName = teacherLastName
        requireStorage().saveToken(token)
    }

    suspend fun hydrateTokenFromStorage(): String? {
        val persistedToken = requireStorage().readToken()
        token = persistedToken
        return persistedToken
    }

    suspend fun clearSession() {
        token = null
        teacherId = null
        teacherFirstName = null
        teacherLastName = null
        requireStorage().clearToken()
    }

    private fun requireStorage(): SecureSessionStorage {
        return sessionStorage ?: error("SessionManager is not initialized")
    }
}
