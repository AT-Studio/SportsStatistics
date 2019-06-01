package com.example.alit.sportsstatistics.utils.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.alit.sportsstatistics.utils.db.tables.Team

@Database(entities = arrayOf(Team::class), version = 1)
abstract class SportsStatisticsDatabase : RoomDatabase() {

    companion object {

        var INSTANCE: SportsStatisticsDatabase? = null

        fun get(context: Context): SportsStatisticsDatabase {
            if (INSTANCE == null) {
                synchronized(SportsStatisticsDatabase::class) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                SportsStatisticsDatabase::class.java, "sportsstatistics_database")
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }
    }

}