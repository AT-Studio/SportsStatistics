package com.example.alit.sportsstatistics.utils.db.tables

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.support.annotation.NonNull

@Entity(tableName = Team.TABLENAME, primaryKeys = arrayOf(Team.ABBREVIATION))
class Team {

    companion object {
        const val TABLENAME = "team_table"
        const val TEAM_ID = "id"
        const val CITY = "city"
        const val NAME = "name"
        const val ABBREVIATION = "abbr"
    }

    @ColumnInfo(name = TEAM_ID)
    var teamID: Int? = null

    @ColumnInfo(name = CITY)
    var city: String? = null

    @ColumnInfo(name = NAME)
    var name: String? = null

    @NonNull
    @ColumnInfo(name = ABBREVIATION)
    var abbr: String? = null
}