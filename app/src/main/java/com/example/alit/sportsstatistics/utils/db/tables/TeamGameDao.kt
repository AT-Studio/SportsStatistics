package com.example.alit.sportsstatistics.utils.db.tables

import android.arch.persistence.room.*
import io.reactivex.Maybe

@Dao
interface TeamGameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(teamGame: TeamGame)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg teamGames: TeamGame)

    @Delete
    fun delete(teamGame: TeamGame)

    @Update
    fun update(teamGame: TeamGame)

    @Query("SELECT * FROM ${TeamGame.TABLENAME} WHERE (${TeamGame.TABLENAME}.${TeamGame.TEAM_ABBR} = :teamAbbr AND ${TeamGame.TABLENAME}.${TeamGame.SEASON}  = :season)")
    fun getTeamGames(teamAbbr: String, season: String): Maybe<List<TeamGame>>

}