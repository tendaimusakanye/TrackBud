package com.tendai.musicx.extensions

import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

fun Fragment.checkSelfPermissionCompat(permission: String) =
    ActivityCompat.checkSelfPermission(this.requireActivity(), permission)

fun Fragment.shouldShowRequestPermissionRationaleCompat(permission: String) =
    ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), permission)

fun Fragment.requestPermissionsCompat(
    permissionsArray: Array<String>,
    requestCode: Int
) = ActivityCompat.requestPermissions(this.requireActivity(), permissionsArray, requestCode)
