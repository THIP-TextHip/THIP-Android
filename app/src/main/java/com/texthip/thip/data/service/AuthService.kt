package com.texthip.thip.data.service

import com.texthip.thip.data.model.auth.request.AuthRequest
import com.texthip.thip.data.model.auth.response.AuthResponse
import com.texthip.thip.data.model.base.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    //신규유저 여부 확인
    @POST("auth/users")
    suspend fun checkNewUser(
        @Body request: AuthRequest
    ): BaseResponse<AuthResponse>
}