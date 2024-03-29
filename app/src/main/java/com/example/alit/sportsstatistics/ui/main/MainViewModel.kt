package com.example.alit.sportsstatistics.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.example.alit.sportsstatistics.datastructures.TeamStandingResponse
import com.example.alit.sportsstatistics.utils.SportsStatisticsRepository
import com.example.alit.sportsstatistics.utils.db.tables.Team
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable

class MainViewModel : AndroidViewModel {

    val sportsStatisticsRepository: SportsStatisticsRepository

    constructor(application: Application) : super (application) {
        sportsStatisticsRepository = SportsStatisticsRepository.get(application)
    }

    fun getTeamStats(season: String, team: String): Observable<TeamStandingResponse> {
        return sportsStatisticsRepository.getTeamStats(season, team)
    }

    fun insertTeamRoom(team: Team): Completable {
        return sportsStatisticsRepository.insertTeamRoom(team)
    }

    fun deleteTeamRoom(team: Team): Completable {
        return sportsStatisticsRepository.deleteTeamRoom(team)
    }

    fun getAllTeamsRoom(): Flowable<List<Team>> {
        return sportsStatisticsRepository.getAllTeamsRoom()
    }

}