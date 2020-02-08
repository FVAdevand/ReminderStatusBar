package ua.fvadevand.reminderstatusbar.utils

import android.util.Log

fun Any.logD(tag: String?, message: String) = Log.d(tag ?: javaClass.simpleName, message)

fun Any.logD(message: String) = logD(null, message)

fun Any.logI(tag: String?, message: String) = Log.i(tag ?: javaClass.simpleName, message)

fun Any.logI(message: String) = logI(null, message)

fun Any.logW(tag: String?, message: String, tr: Throwable? = null) =
    Log.w(tag ?: javaClass.simpleName, message, tr)

fun Any.logW(message: String, tr: Throwable? = null) = logW(null, message, tr)

fun Any.logE(tag: String?, message: String, tr: Throwable? = null) =
    Log.e(tag ?: javaClass.simpleName, message, tr)

fun Any.logE(message: String, tr: Throwable? = null) = logE(null, message, tr)