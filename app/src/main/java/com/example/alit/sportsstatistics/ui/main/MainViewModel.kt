package com.example.alit.sportsstatistics.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.example.alit.sportsstatistics.datastructures.TeamStandingResponse
import com.example.alit.sportsstatistics.utils.SportsStatisticsRepository
import io.reactivex.Observable

class MainViewModel : AndroidViewModel {

    val sportsStatisticsRepository: SportsStatisticsRepository

    constructor(application: Application) : super (application) {
        sportsStatisticsRepository = SportsStatisticsRepository.get(application)
    }

    fun getTeamStats(team: String): Observable<TeamStandingResponse> {
        return sportsStatisticsRepository.getTeamStats(team)
    }

}