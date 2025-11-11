package com.texthip.thip.utils.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object NotificationPermissionUtils {


    // POST_NOTIFICATIONS 권한이 필요한지 확인
    fun isNotificationPermissionRequired(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }


    // 알림 권한이 허용되었는지 확인
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (isNotificationPermissionRequired()) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 이하에서는 권한이 자동으로 허용됨
            true
        }
    }


    // 알림 권한 요청이 필요한지 확인
    fun shouldRequestNotificationPermission(context: Context): Boolean {
        return isNotificationPermissionRequired() && !isNotificationPermissionGranted(context)
    }
}