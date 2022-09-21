package dev.ciox.ratiba.components.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build


class PermissionManager(var context: Context) {

    val readStorageGranted: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            else true
        }
}