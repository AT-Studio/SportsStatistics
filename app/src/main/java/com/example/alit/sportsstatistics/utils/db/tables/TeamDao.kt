package com.example.alit.sportsstatistics.utils.db.tables

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe

@Dao
interface TeamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(team: Team)

    @Delete
    fun delete(team: Team)

    @Update
    fun update(team: Team)

    @Query("SELECT * FROM ${Team.TABLENAME} WHERE ${Team.TABLENAME}.${Team.TEAM_ID} = :teamID")
    fun getTeam(teamID: Int): Maybe<Team>

    @Query("SELECT * FROM ${Team.TABLENAME}")
    fun getAllTeams(): Flowable<List<Team>>

}