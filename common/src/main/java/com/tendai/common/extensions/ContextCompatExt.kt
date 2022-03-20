package com.tendai.common.extensions

import android.content.Context
import androidx.core.content.ContextCompat

fun Context.checkSelfPermissionCompat(permission: String) =
    ContextCompat.checkSelfPermission(this, permission)