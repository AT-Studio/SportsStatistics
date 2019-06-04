package com.example.alit.sportsstatistics.utils

import android.content.Context
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.datastructures.TeamGamesResponse
import com.example.alit.sportsstatistics.datastructures.TeamStandingResponse
import com.example.alit.sportsstatistics.utils.db.SportsStatisticsDatabase
import com.example.alit.sportsstatistics.utils.db.tables.Team
import com.example.alit.sportsstatistics.utils.db.tables.TeamGame
import com.example.alit.sportsstatistics.utils.db.tables.TeamStandings
import com.example.alit.sportsstatistics.utils.network.SportsStatisticsService
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import okhttp3.Credentials
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class SportsStatisticsRepository {

    companion object {

        const val BASE_URL = "https://api.mysportsfeeds.com/v2.1/pull/nfl/"
        const val SEASON_LATEST = "Latest"
        const val SEASON_2018_REGULAR = "2018-Regular"
        const val SEASON_2018_PLAYOFFS = "2018-Playoffs"
        const val SEASON_2017_REGULAR = "2017-Regular"
        const val SEASON_2017_PLAYOFFS = "2017-Playoffs"
        const val SEASON_2016_REGULAR = "2016-Regular"
        const val SEASON_2016_PLAYOFFS = "2016-Playoffs"
        const val SEASON_2015_REGULAR = "2015-Regular"
        const val SEASON_2015_PLAYOFFS = "2015-Playoffs"


        var INSTANCE: SportsStatisticsRepository? = null

        fun get(context: Context): SportsStatisticsRepository {
            if (INSTANCE == null) {
                synchronized(SportsStatisticsRepository::class) {
                    if (INSTANCE == null) {
                        INSTANCE = SportsStatisticsRepository()
                        INSTANCE!!.context = context.applicationContext
                        INSTANCE!!.sportsStatisticsDatabase = SportsStatisticsDatabase.get(context)
                    }
                }
            }
            return INSTANCE!!
        }

    }

    lateinit var context: Context
    lateinit var sportsStatisticsDatabase: SportsStatisticsDatabase

    fun getTeamStats(season: String, team: String): Observable<TeamStandingResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val sportsStatsAPI = retrofit.create(SportsStatisticsService::class.java)

        return sportsStatsAPI.getTeamStandings(getApiAuth(), season, team)
    }

    fun getTeamGames(season: String, team: String): Observable<TeamGamesResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val sportsGamesAPI = retrofit.create(SportsStatisticsService::class.java)

        return sportsGamesAPI.getTeamGames(getApiAuth(), season, team)
    }

    fun getApiAuth(): String {
        return Credentials.basic(context.resources.getString(R.string.my_sports_feed_API_key), "MYSPORTSFEEDS")
//        val auth = "9bb37a05-a9fa-4489-ad71-32c526:MYSPORTSFEEDS"
//        return "Basic " + auth
    }

    fun insertTeamRoom(team: Team): Completable {
        return Completable.fromCallable {
            sportsStatisticsDatabase.teamDao().insert(team)
        }
    }

    fun deleteTeamRoom(team: Team): Completable {
        return Completable.fromCallable {
            sportsStatisticsDatabase.teamDao().delete(team)
        }
    }

    fun getAllTeamsRoom(): Flowable<List<Team>> {
        return sportsStatisticsDatabase.teamDao().getAllTeams()
    }

    fun insertTeamStandingRoom(teamStanding: TeamStandings): Completable {
        return Completable.fromCallable {
            sportsStatisticsDatabase.teamStandingsDao().insert(teamStanding)
        }
    }

    fun getTeamStandingRoom(teamAbbr: String, season: String): Maybe<TeamStandings> {
        return sportsStatisticsDatabase.teamStandingsDao().getTeamStanding(teamAbbr, season)
    }

    fun getTeamGamesRoom(teamAbbr: String, season: String): Maybe<List<TeamGame>> {
        return sportsStatisticsDatabase.teamGamesDao().getTeamGames(teamAbbr, season)
    }

    fun insertAllTeamGamesRoom(teamGames: List<TeamGame>): Completable {
        return Completable.fromCallable {
            sportsStatisticsDatabase.teamGamesDao().insertAll(*teamGames.toTypedArray())
        }
    }
}