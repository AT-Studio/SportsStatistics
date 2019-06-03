package com.example.alit.sportsstatistics.ui.team_detail

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.Log
import com.example.alit.sportsstatistics.R
import com.example.alit.sportsstatistics.ui.base.BaseActivity
import com.example.alit.sportsstatistics.utils.SportsStatisticsRepository
import com.example.alit.sportsstatistics.utils.db.tables.TeamStandings
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_team_detail.*

class TeamDetailActivity : BaseActivity() {

    companion object {

        const val TEAM_NAME_EXTRA = "name"
        const val TEAM_CITY_EXTRA = "city"
        const val TEAM_ABBREVIATION_EXTRA = "team"

    }

    lateinit var viewModel: TeamDetailViewModel

    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_detail)
//        setSupportActionBar(tb_activity_team_detail)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }


        appbar_activity_team_detail.post {
            val appbarParams = appbar_activity_team_detail.layoutParams as CoordinatorLayout.LayoutParams
            appbarParams.height = cl_activity_main_dashboard.height + getPixels(56) + getStatusBarHeight()
            appbar_activity_team_detail.layoutParams = appbarParams
        }

        val bundle = intent.extras
        if (bundle == null || bundle.isEmpty) finishActivity()

        val teamName = bundle.getString(TEAM_NAME_EXTRA)
        val teamCity = bundle.getString(TEAM_CITY_EXTRA)
        val teamAbbr = bundle.getString(TEAM_ABBREVIATION_EXTRA)

        viewModel = ViewModelProviders.of(this).get(TeamDetailViewModel::class.java)

        //TODO: first check if season is stored locally?

        disposable = viewModel.getTeamStandingRoom(teamAbbr, SportsStatisticsRepository.SEASON_2018_REGULAR)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ teamStanding ->
                    Log.d("room", "onSuccess")
                    iv_activity_team_detail_logo.setImageDrawable(
                            ContextCompat.getDrawable(this, getId(teamAbbr.toLowerCase(), R.drawable::class.java))
                    )
                    tv_activity_team_detail_name.setText(teamName)
                    tv_activity_team_detail_city.setText(teamCity)
                    tv_activity_team_detail_wins.setText(teamStanding.wins!!.toString())
                    tv_activity_team_detail_losses.setText(teamStanding.losses!!.toString())
                    tv_activity_team_detail_ties.setText(teamStanding.ties!!.toString())
                    tv_activity_team_detail_rank.setText(teamStanding.rank!!.toString())
                }, {
                    Log.d("room", it.message)
                }, {
                    Log.d("room", "onComplete")
                    disposable = viewModel.getTeamStats(SportsStatisticsRepository.SEASON_2018_REGULAR, teamAbbr)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe ({ teamStanding ->
                                val team = teamStanding!!.teams!![0]
                                iv_activity_team_detail_logo.setImageDrawable(
                                        ContextCompat.getDrawable(this, getId(team.team!!.abbreviation!!.toLowerCase(), R.drawable::class.java))
                                )
                                tv_activity_team_detail_name.setText(teamName)
                                tv_activity_team_detail_city.setText(teamCity)
                                tv_activity_team_detail_wins.setText(team.stats!!.standings!!.wins!!.toString())
                                tv_activity_team_detail_losses.setText(team.stats.standings!!.losses!!.toString())
                                tv_activity_team_detail_ties.setText(team.stats.standings.ties!!.toString())
                                tv_activity_team_detail_rank.setText(team.overallRank!!.rank!!.toString())
                                val teamStandingRoom = TeamStandings()
                                teamStandingRoom.teamAbbr = teamAbbr
                                teamStandingRoom.season = SportsStatisticsRepository.SEASON_2018_REGULAR
                                teamStandingRoom.gamesPlayed = team.stats.gamesPlayed
                                teamStandingRoom.wins = team.stats.standings.wins
                                teamStandingRoom.losses = team.stats.standings.losses
                                teamStandingRoom.ties = team.stats.standings.ties
                                teamStandingRoom.rank = team.overallRank.rank
                                disposable = viewModel.insertTeamStandingRoom(teamStandingRoom)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe ({
                                            Log.d("room", "completed saving team standing")
                                        }, {
                                            Log.d("room", it.message)
                                        })
                            }, {
                                Log.d("mySports", it.message)
                            })
                })

        cl_activity_main_dashboard.post {
            val dashParams = cl_activity_main_dashboard.layoutParams as CollapsingToolbarLayout.LayoutParams
            dashParams.topMargin = getPixels(56) + getStatusBarHeight()
            cl_activity_main_dashboard.layoutParams = dashParams
        }

        appbar_activity_team_detail.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            Log.d("teamdetail", "offset: $verticalOffset")
            if (Math.abs(verticalOffset) == appbar_activity_team_detail.totalScrollRange) {
                tb_activity_team_detail.elevation = getPixels(0).toFloat()
                ViewCompat.setElevation(appbar_activity_team_detail, getPixels(2).toFloat())
            } else if (verticalOffset == 0) {
                tb_activity_team_detail.elevation = getPixels(0).toFloat()
                ViewCompat.setElevation(appbar_activity_team_detail, 0f)
            } else {
                tb_activity_team_detail.elevation = getPixels(2).toFloat()
                ViewCompat.setElevation(appbar_activity_team_detail, 0f)
            }
        }
    }

    fun getId(resourceName: String, c: Class<*>): Int {
        try {
            val idField = c.getDeclaredField(resourceName)
            return idField.getInt(idField)
        } catch (e: Exception) {
            throw RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (disposable != null && !disposable!!.isDisposed) disposable!!.dispose()
    }
}
