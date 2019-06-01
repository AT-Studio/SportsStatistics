package com.example.alit.sportsstatistics.utils

import android.content.Context
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.datastructures.TeamStandingResponse
import com.example.alit.sportsstatistics.utils.db.SportsStatisticsDatabase
import com.example.alit.sportsstatistics.utils.network.SportsStatisticsService
import io.reactivex.Observable
import okhttp3.Credentials
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


class SportsStatisticsRepository {

    companion object {

        const val BASE_URL = "https://api.mysportsfeeds.com/v2.1/pull/nfl/"

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

    fun getTeamStats(team: String): Observable<TeamStandingResponse> {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        val sportsStatsAPI = retrofit.create(SportsStatisticsService::class.java)

        return sportsStatsAPI.getTeamStandings(getApiAuth(), team)
    }

    fun getApiAuth(): String {
        return Credentials.basic(context.resources.getString(R.string.my_sports_feed_API_key), "MYSPORTSFEEDS")
//        val auth = "9bb37a05-a9fa-4489-ad71-32c526:MYSPORTSFEEDS"
//        return "Basic " + auth
    }

}