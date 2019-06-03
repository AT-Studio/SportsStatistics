package com.example.alit.sportsstatistics.ui.team_detail

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.example.alit.sportsstatistics.datastructures.TeamStandingResponse
import com.example.alit.sportsstatistics.utils.SportsStatisticsRepository
import com.example.alit.sportsstatistics.utils.db.tables.TeamStandings
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable

class TeamDetailViewModel : AndroidViewModel {

    val sportsStatisticsRepository: SportsStatisticsRepository

    constructor(application: Application) : super (application) {
        sportsStatisticsRepository = SportsStatisticsRepository.get(application)
    }

    fun getTeamStats(season: String, team: String): Observable<TeamStandingResponse> {
        return sportsStatisticsRepository.getTeamStats(season, team)
    }

    fun insertTeamStandingRoom(teamStanding: TeamStandings): Completable {
        return sportsStatisticsRepository.insertTeamStandingRoom(teamStanding)
    }

    fun getTeamStandingRoom(teamAbbr: String, season: String): Maybe<TeamStandings> {
        return sportsStatisticsRepository.getTeamStandingRoom(teamAbbr, season)
    }

}