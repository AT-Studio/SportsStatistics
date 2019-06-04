package com.example.alit.sportsstatistics.utils.network

import com.example.alit.sportsstatistics.datastructures.TeamGamesResponse
import com.example.alit.sportsstatistics.datastructures.TeamStandingResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SportsStatisticsService {

    @GET("{season}/standings.json")
    fun getTeamStandings(@Header("Authorization") auth: String,
                         @Path("season") season: String,
                         @Query("team") team: String): Observable<TeamStandingResponse>

    @GET("{season}/games.json")
    fun getTeamGames(@Header("Authorization") auth: String,
                     @Path("season") season: String,
                     @Query("team") team: String): Observable<TeamGamesResponse>
}