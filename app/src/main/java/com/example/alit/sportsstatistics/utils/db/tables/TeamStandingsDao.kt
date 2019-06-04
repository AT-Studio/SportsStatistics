package com.example.alit.sportsstatistics.utils.db.tables

import android.arch.persistence.room.*
import io.reactivex.Maybe

@Dao
interface TeamStandingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(teamStanding: TeamStandings)

    @Delete
    fun delete(teamStanding: TeamStandings)

    @Update
    fun update(teamStanding: TeamStandings)

    @Query("SELECT * FROM ${TeamStandings.TABLENAME} WHERE (${TeamStandings.TABLENAME}.${TeamStandings.TEAM_ABBREVIATION} = :teamAbbr AND " +
            "${TeamStandings.TABLENAME}.${TeamStandings.SEASON} = :season)")
    fun getTeamStanding(teamAbbr: String, season: String): Maybe<TeamStandings>

    @Query("DELETE FROM ${TeamStandings.TABLENAME} WHERE ${TeamStandings.TEAM_ABBREVIATION} = :teamAbbr")
    fun deleteTeamStanding(teamAbbr: String)

}