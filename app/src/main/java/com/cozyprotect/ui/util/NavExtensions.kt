package com.cozyprotect.ui.util

import androidx.navigation.NavHostController

fun NavHostController.safeNavigate(route: String) {
    if (currentDestination?.route == route) return
    runCatching { navigate(route) }
        .onFailure { GlobalErrorHandler.report(it) }
}
