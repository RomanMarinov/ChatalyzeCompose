package com.dev_marinov.chatalyze.presentation.util

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
fun PermissionState.isAlwaysDenied() : Boolean {
    return !status.isGranted && !status.shouldShowRationale
}