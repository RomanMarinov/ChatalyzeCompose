package com.dev_marinov.chatalyze.presentation.util

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@OptIn(ExperimentalPermissionsApi::class)
fun PermissionState.isAlwaysDenied() : Boolean {
    return !hasPermission && !shouldShowRationale
}