package com.example.alit.sportsstatistics.utils.db.tables

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.support.annotation.NonNull

@Entity(tableName = Team.TABLENAME, primaryKeys = arrayOf(Team.TEAM_ID))
class Team {

    companion object {
        const val TABLENAME = "team_table"
        const val TEAM_ID = "team_id"
    }

    @NonNull
    @ColumnInfo(name = TEAM_ID)
    var teamID: String? = null

}