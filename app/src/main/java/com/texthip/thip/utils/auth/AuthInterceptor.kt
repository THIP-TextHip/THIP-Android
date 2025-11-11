package com.texthip.thip.utils.auth

import com.texthip.thip.data.manager.AuthStateManager
import com.texthip.thip.data.manager.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authStateManager: AuthStateManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        if (original.header("Authorization") != null) {
            return chain.proceed(original)
        }

        val token = runBlocking { tokenManager.getTokenOnce() }
        val tempToken = runBlocking { tokenManager.getTempTokenOnce() }

        val tokenToSend = token ?: tempToken

        val newRequest = original.newBuilder().apply {
            tokenToSend?.let { addHeader("Authorization", "Bearer $it") }
        }.build()

        val response = chain.proceed(newRequest)

        // 401 또는 인증 관련 500 에러 처리
        val shouldClearAuth = response.code == 401 ||
            (response.code == 500 && isAuthError(response))

        if (shouldClearAuth) {
            runBlocking {
                tokenManager.clearTokens()
                authStateManager.triggerTokenExpired()
            }
        }

        return response
    }

    private fun isAuthError(response: Response): Boolean {
        return try {
            val body = response.peekBody(Long.MAX_VALUE).string()
            // 에러 코드 40108 (인증 처리 중 서버 오류) 확인
            body.contains("40108")
        } catch (e: Exception) {
            false
        }
    }
}
