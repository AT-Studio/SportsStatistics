package com.example.alit.sportsstatistics.utils.db.tables

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.support.annotation.NonNull

@Entity(tableName = TeamGame.TABLENAME, primaryKeys = arrayOf(TeamGame.ID, TeamGame.TEAM_ABBR))
class TeamGame {

    companion object {
        const val TABLENAME = "game_table"
        const val ID = "id"
        const val START_TIME = "start_time"
        const val AWAY_TEAM_ABBR = "away_team_abbr"
        const val HOME_TEAM_ABBR = "home_team_abbr"
        const val AWAY_SCORE = "away_score"
        const val HOME_SCORE = "home_score"
        const val TEAM_ABBR = "team_abbr"
        const val SEASON = "season"
    }

    @NonNull
    @ColumnInfo(name = ID)
    var id: Int? = null

    @ColumnInfo(name = START_TIME)
    var startTime: String? = null

    @ColumnInfo(name = AWAY_TEAM_ABBR)
    var awayTeamAbbr: String? = null

    @ColumnInfo(name = HOME_TEAM_ABBR)
    var homeTeamAbbr: String? = null

    @ColumnInfo(name = AWAY_SCORE)
    var awayScore: Int? = null

    @ColumnInfo(name = HOME_SCORE)
    var homeScore: Int? = null

    @NonNull
    @ColumnInfo(name = TEAM_ABBR)
    var teamAbbr: String? = null

    @ColumnInfo(name = SEASON)
    var season: String? = null

}