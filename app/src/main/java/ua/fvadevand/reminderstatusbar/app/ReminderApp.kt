package ua.fvadevand.reminderstatusbar.app

import android.app.Application

import androidx.room.Room

import ua.fvadevand.reminderstatusbar.Const
import ua.fvadevand.reminderstatusbar.data.database.AppDatabase

class ReminderApp : Application() {

    val db: AppDatabase by lazy { Room.databaseBuilder(this, AppDatabase::class.java, Const.DATABASE_NAME).build() }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: ReminderApp
    }
}
