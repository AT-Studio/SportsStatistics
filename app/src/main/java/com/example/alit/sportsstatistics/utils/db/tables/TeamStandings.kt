package com.example.alit.sportsstatistics.utils.db.tables

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.support.annotation.NonNull

@Entity(tableName = TeamStandings.TABLENAME, primaryKeys = arrayOf(TeamStandings.TEAM_ABBREVIATION, TeamStandings.SEASON))
class TeamStandings {

    companion object {
        const val TABLENAME = "team_standings_table"
        const val TEAM_ABBREVIATION = "team_abbr"
        const val SEASON = "season"
        const val GAMES_PLAYED = "games_played"
        const val WINS = "wins"
        const val LOSSES = "losses"
        const val TIES = "ties"
        const val RANK = "rank"
    }

    @NonNull
    @ColumnInfo(name = TEAM_ABBREVIATION)
    var teamAbbr: String? = null

    @NonNull
    @ColumnInfo(name = SEASON)
    var season: String? = null

    @ColumnInfo(name = GAMES_PLAYED)
    var gamesPlayed: Int? = null

    @ColumnInfo(name = WINS)
    var wins: Int? = null

    @ColumnInfo(name = LOSSES)
    var losses: Int? = null

    @ColumnInfo(name = TIES)
    var ties: Int? = null

    @ColumnInfo(name = RANK)
    var rank: Int? = null
}