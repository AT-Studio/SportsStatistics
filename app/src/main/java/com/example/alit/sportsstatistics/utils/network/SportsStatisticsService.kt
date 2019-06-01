package com.example.alit.sportsstatistics.utils.network

import com.example.alit.sportsstatistics.datastructures.TeamStandingResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SportsStatisticsService {

    @GET("latest/standings.json")
    fun getTeamStandings(@Header("Authorization") auth: String, @Query("team") name: String): Observable<TeamStandingResponse>

}