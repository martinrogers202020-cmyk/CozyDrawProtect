package com.cozyprotect.ui.util

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object GlobalErrorHandler {
    private val _errors = MutableStateFlow<Throwable?>(null)
    val errors = _errors.asStateFlow()

    fun report(throwable: Throwable) {
        Handler(Looper.getMainLooper()).post {
            _errors.value = throwable
        }
    }

    fun clear() {
        _errors.value = null
    }
}
