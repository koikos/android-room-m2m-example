package com.example.roommanytomany

import android.app.Application
import com.example.roommanytomany.data.AirportDao
import com.example.roommanytomany.data.AirportDatabase

class RoomM2MApplication : Application() {
    lateinit var airportDao: AirportDao
    override fun onCreate() {
        super.onCreate()
        airportDao = AirportDatabase.getDatabase(context = this).airportDao()
    }
}