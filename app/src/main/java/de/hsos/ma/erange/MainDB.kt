package de.hsos.ma.erange

import android.app.Application
import androidx.room.Room

class MainDB: Application() {

    companion object {
        lateinit var entryDatabase: EntryDatabase
    }

    override fun onCreate() {
        super.onCreate()
        entryDatabase = Room.databaseBuilder(
            applicationContext,
            EntryDatabase::class.java,
            EntryDatabase.NAME
        ).build()
    }

}