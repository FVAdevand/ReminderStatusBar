package ua.fvadevand.reminderstatusbar.utilities

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AppExecutors private constructor() {
    val diskIO: Executor = Executors.newSingleThreadExecutor()

    companion object {
        val instance: AppExecutors by lazy { AppExecutors() }
    }
}
